import {Component, computed, DestroyRef, inject, resource, signal} from '@angular/core'
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute, Router, RouterModule} from '@angular/router'
import {interval, map} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {MatChipsModule} from '@angular/material/chips'
import {MatDialogModule, MatDialog} from '@angular/material/dialog'
import {MatDividerModule} from '@angular/material/divider'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameSessionPlayer, WorkflowBattleAttackFrontRequest, WorkflowBattleCreateFrontRequest, BattleFront, BattleFrontUnit, GameUnit} from '@board-buddy/core'
import {PlayerService, PortalWorkflowService} from '@board-buddy/portal'
import {PortalBattle} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'
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

  private workflowResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getWorkflow(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private battleResource = resource({
    params: computed(() => ({key: this.sessionKey(), hasBattle: !!this.workflowResource.value()?.activeBattle})),
    loader: (p) => p.params.key && p.params.hasBattle
      ? toPromise(this.workflowService.getBattle(p.params.key), p.abortSignal).catch(() => null as PortalBattle | null)
      : Promise.resolve(null as PortalBattle | null)
  })

  readonly workflow = computed(() => this.workflowResource.value())
  readonly sessionName = computed(() => this.workflow()?.name ?? '')
  readonly sessionId = computed(() => this.workflow()?.id ?? '')
  readonly hostId = computed(() => this.workflow()?.host.id ?? null)
  readonly participants = computed(() => this.workflow()?.participants ?? [])
  readonly myParticipant = computed(() => this.participants().find(p => p.player.id === this.playerId()))
  readonly isHost = computed(() => this.workflow()?.host.id === this.playerId())
  readonly hasBattle = computed(() => !!this.workflow()?.activeBattle)
  readonly battle = computed(() => this.battleResource.value() ?? null)
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
  readonly showQr = signal(false)

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
    interval(5000).pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.workflowResource.reload()
    })
    interval(1000).pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      if (this.hasBattle() && !this.isMyTurn() && !this.battleFinished()) this.battleResource.reload()
    })
  }

  toggleQr() { this.showQr.set(!this.showQr()) }

  back() { this.router.navigate(['/home']) }

  reload() {
    this.workflowResource.reload()
    if (this.hasBattle()) this.battleResource.reload()
  }

  attackPlayer(defender: GameSessionPlayer) {
    const key = this.sessionKey()
    const attacker = this.myParticipant()
    if (!key || !attacker) return
    this.dialog.open(SessionBattleStartDialogComponent, {data: {sessionKey: key, attacker: attacker.player, defender: defender.player}, maxWidth: '95vw', width: '480px'})
      .afterClosed().subscribe(saved => { if (saved) { this.workflowResource.reload(); this.battleResource.reload() } })
  }

  selectUnit(unit: GameUnit) {
    this.selectedUnit.set(this.selectedUnit()?.entity === unit.entity ? null : unit)
  }

  createFront(unit: GameUnit) {
    const key = this.sessionKey()
    const pid = this.playerId()
    if (!key || !pid) return
    this.workflowService.battleCreateFront(key, new WorkflowBattleCreateFrontRequest(pid, unit.entity)).subscribe({
      next: (battle) => { this.battleResource.set(battle); this.selectedUnit.set(null) },
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
        this.battleResource.set(result)
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
        this.battleResource.set(null)
        this.workflowResource.reload()
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
    const map: Record<string, string> = {INFANTRY: '/img/infantry2.jpg', MOUNTED: '/img/cavalry2.jpg', ARTILLERY: '/img/artillery2.jpg', AIRCRAFT: '/img/plane2.jpg'}
    return map[kind] ?? null
  }

}
