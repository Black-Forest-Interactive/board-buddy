import {Component, computed, DestroyRef, inject, signal} from '@angular/core'
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute, Router, RouterModule} from '@angular/router'
import {catchError, combineLatest, EMPTY, filter, interval, map, of, startWith, switchMap} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {MatChipsModule} from '@angular/material/chips'
import {MatDialogModule, MatDialog} from '@angular/material/dialog'
import {MatDividerModule} from '@angular/material/divider'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {BattleFront, BattleFrontUnit, GameSessionPlayer, GameUnit, Nation, Workflow, WorkflowBattleAttackFrontRequest, WorkflowBattleCreateFrontRequest, WorkflowParticipantInfo} from '@board-buddy/core'
import {PlayerService, PortalBattle, PortalWorkflowService} from '@board-buddy/portal'
import {SessionBattleStartDialogComponent} from './session-battle-start-dialog/session-battle-start-dialog.component'

@Component({
  selector: 'portal-session',
  imports: [RouterModule, MatButtonModule, MatIconModule, MatCardModule, MatChipsModule, MatDialogModule, MatDividerModule, MatTooltipModule, TranslatePipe],
  templateUrl: './session.component.html',
})
export class SessionComponent {
  private workflowService = inject(PortalWorkflowService)
  private playerService = inject(PlayerService)
  private dialog = inject(MatDialog)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)
  private destroyRef = inject(DestroyRef)

  private sessionKey = toSignal(this.route.paramMap.pipe(map(p => p.get('key') ?? '')))
  readonly playerId = computed(() => this.playerService.getPlayerId())

  private readonly workflowData = signal<Workflow | undefined>(undefined)
  private readonly participantsData = signal<WorkflowParticipantInfo[]>([])
  private readonly battleData = signal<PortalBattle | null>(null)

  readonly workflow = computed(() => this.workflowData())
  readonly sessionName = computed(() => this.workflow()?.name ?? '')
  readonly sessionId = computed(() => this.workflow()?.id ?? '')
  readonly hostId = computed(() => this.workflow()?.host.id ?? null)
  readonly participants = computed(() => this.workflow()?.participants ?? [])
  readonly myParticipant = computed(() => this.participants().find(p => p.player.id === this.playerId()))

  private nationById = computed(() => new Map<number, Nation>((this.workflow()?.ruleSet.nations ?? []).map(n => [n.id, n])))
  private infoByPlayerId = computed(() => new Map<number, WorkflowParticipantInfo>(this.participantsData().map(i => [i.player.id, i])))
  readonly participantsEnriched = computed(() => this.participants().map(p => ({
    participant: p,
    nation: this.nationById().get(this.infoByPlayerId().get(p.player.id)?.nation?.id ?? -1) ?? null,
    government: this.infoByPlayerId().get(p.player.id)?.government ?? null
  })))
  readonly isHost = computed(() => this.workflow()?.host.id === this.playerId())
  readonly hasBattle = computed(() => !!this.workflow()?.activeBattle)
  readonly battle = computed(() => this.battleData())
  readonly battleStatus = computed(() => this.battle()?.status ?? null)
  readonly battleFinished = computed(() => this.battleStatus() === 'FINISHED')
  readonly activePlayerId = computed(() => this.battle()?.activePlayer.player.id ?? null)
  readonly isMyTurn = computed(() => this.activePlayerId() === this.playerId())
  readonly battleWinner = computed(() => this.battle()?.winner ?? null)
  readonly myBattleInfo = computed(() => this.battle()?.myInfo ?? null)
  readonly opponentInfo = computed(() => this.battle()?.opponentInfo ?? null)
  readonly fronts = computed(() => this.battle()?.fronts ?? [])
  readonly logEntries = computed(() => this.battle()?.logEntries ?? [])
  readonly selectedUnit = signal<GameUnit | null>(null)

  private readonly showQrOverride = signal<boolean | null>(null)
  readonly showQr = computed(() => this.showQrOverride() ?? this.participants().length === 1)

  readonly joinUrl = computed(() => {
    const key = this.sessionId()
    if (!key) return ''
    return `${window.location.origin}/session/join?key=${key}`
  })

  readonly qrUrl = computed(() => {
    const url = this.joinUrl()
    return url ? `https://api.qrserver.com/v1/create-qr-code/?size=256x256&data=${encodeURIComponent(url)}` : ''
  })

  readonly reserveUnits = computed(() => {
    const mine = this.myBattleInfo()
    if (!mine) return []
    const onFront = new Set(this.fronts().flatMap(f => f.units.filter(fu => fu.player.player.id === this.playerId()).map(fu => fu.unit.entity)))
    return mine.units.filter(u => !onFront.has(u.entity))
  })

  readonly attackableFronts = computed(() => {
    const battle = this.battle()
    if (!battle || !this.isMyTurn()) return new Set<number>()
    const onFront = new Set(this.fronts().flatMap(f => f.units.filter(fu => fu.player.player.id === this.playerId()).map(fu => fu.unit.entity)))
    if (!battle.myInfo.units.some(u => !onFront.has(u.entity))) return new Set<number>()
    return new Set(this.fronts().filter(f =>
      f.units.some(fu => fu.player.player.id !== this.playerId()) && !f.units.some(fu => fu.player.player.id === this.playerId())
    ).map(f => f.index))
  })

  constructor() {
    interval(5000).pipe(
      startWith(0),
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.fetchSessionData())
    ).subscribe(data => {
      this.workflowData.set(data.workflow)
      this.participantsData.set(data.participants)
      this.battleData.set(data.battle)
    })

    interval(1000).pipe(
      takeUntilDestroyed(this.destroyRef),
      filter(() => !!this.battleData() && !this.isMyTurn() && !this.battleFinished()),
      switchMap(() => {
        const key = this.sessionKey()
        return key ? this.workflowService.getBattle(key).pipe(catchError(() => EMPTY)) : EMPTY
      })
    ).subscribe(battle => this.battleData.set(battle))
  }

  private fetchSessionData() {
    const key = this.sessionKey()
    if (!key) return EMPTY
    return combineLatest([
      this.workflowService.getWorkflow(key),
      this.workflowService.getParticipantsInfo(key)
    ]).pipe(
      switchMap(([workflow, participants]) => {
        if (!workflow.activeBattle) return of({workflow, participants, battle: null as PortalBattle | null})
        return this.workflowService.getBattle(key).pipe(
          map(battle => ({workflow, participants, battle})),
          catchError(() => of({workflow, participants, battle: null as PortalBattle | null}))
        )
      })
    )
  }

  private reloadAll() {
    this.fetchSessionData().subscribe(data => {
      this.workflowData.set(data.workflow)
      this.participantsData.set(data.participants)
      this.battleData.set(data.battle)
    })
  }

  toggleQr() { this.showQrOverride.set(!this.showQr()) }
  copyLink() { navigator.clipboard.writeText(this.joinUrl()) }
  back() { this.router.navigate(['/home']) }
  reload() { this.reloadAll() }

  attackPlayer(defender: GameSessionPlayer) {
    const key = this.sessionKey()
    const attacker = this.myParticipant()
    if (!key || !attacker) return
    this.dialog.open(SessionBattleStartDialogComponent, {data: {sessionKey: key, attacker: attacker.player, defender: defender.player}, maxWidth: '95vw', width: '480px'})
      .afterClosed().subscribe(saved => { if (saved) this.reloadAll() })
  }

  selectUnit(unit: GameUnit) {
    this.selectedUnit.set(this.selectedUnit()?.entity === unit.entity ? null : unit)
  }

  createFront(unit: GameUnit) {
    const key = this.sessionKey()
    const pid = this.playerId()
    if (!key || !pid) return
    this.workflowService.battleCreateFront(key, new WorkflowBattleCreateFrontRequest(pid, unit.entity)).subscribe({
      next: (battle) => { this.battleData.set(battle); this.selectedUnit.set(null) },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  attackFront(frontIndex: number) {
    const key = this.sessionKey()
    const unit = this.selectedUnit()
    const pid = this.playerId()
    const battle = this.battle()
    if (!key || !unit || !pid || !battle) return
    const opponentId = battle.opponentInfo.player.player.id
    this.workflowService.battleAttackFront(key, new WorkflowBattleAttackFrontRequest(pid, opponentId, unit.entity, frontIndex)).subscribe({
      next: (result) => {
        this.translate.get('session.battle.attacked').subscribe(t => this.toast.success(t))
        this.battleData.set(result)
        this.selectedUnit.set(null)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  finishBattle() {
    const key = this.sessionKey()
    if (!key) return
    this.workflowService.battleFinish(key).subscribe({
      next: () => {
        this.battleData.set(null)
        this.reloadAll()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  getFrontUnitForMe(front: BattleFront): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id === this.playerId())
  }

  getFrontUnitForOpponent(front: BattleFront): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id !== this.playerId())
  }

  hpBoxes(current: number, max: number | null | undefined): boolean[] {
    return Array.from({length: max ?? 0}, (_, i) => i < current)
  }

  range(n: number | null | undefined): number[] {
    return Array.from({length: n ?? 0}, (_, i) => i)
  }

  unitImagePath(kind: string | null | undefined): string | null {
    if (!kind) return null
    const map: Record<string, string> = {INFANTRY: '/img/unit/infantry2_mini.jpg', MOUNTED: '/img/unit/cavalry2_mini.jpg', ARTILLERY: '/img/unit/artillery2_mini.jpg', AIRCRAFT: '/img/unit/plane2_mini.jpg'}
    return map[kind] ?? null
  }
}
