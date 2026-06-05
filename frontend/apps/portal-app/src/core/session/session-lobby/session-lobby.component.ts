import {Component, computed, DestroyRef, inject, signal} from '@angular/core'
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop'
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout'
import {ActivatedRoute, Router, RouterModule} from '@angular/router'
import {catchError, combineLatest, EMPTY, interval, map, of, startWith, switchMap} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatDialog, MatDialogModule} from '@angular/material/dialog'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameSessionPlayer, GameUnit, Nation, Workflow, WorkflowBattleAttackFrontRequest, WorkflowBattleCreateFrontRequest, WorkflowParticipantInfo} from '@board-buddy/core'
import {MainContentComponent} from '@board-buddy/ui'
import {PlayerService, PortalBattle, PortalWorkflowService, TourService} from '@board-buddy/portal'
import {SessionBattleStartDialogComponent} from '../session-battle-start-dialog/session-battle-start-dialog.component'
import {SessionBattleComponent} from '../session-battle/session-battle.component'
import {SessionContentComponent} from '../session-content/session-content.component'
import {SessionQrcodeDialogComponent} from '../session-qrcode-dialog/session-qrcode-dialog.component'

@Component({
  selector: 'portal-session-lobby',
  imports: [RouterModule, MatButtonModule, MatIconModule, MatDialogModule, MatTooltipModule, TranslatePipe, MainContentComponent, SessionBattleComponent, SessionContentComponent],
  templateUrl: './session-lobby.component.html',
})
export class SessionLobbyComponent {
  private workflowService = inject(PortalWorkflowService)
  private playerService = inject(PlayerService)
  private tourService = inject(TourService)
  private dialog = inject(MatDialog)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)
  private destroyRef = inject(DestroyRef)
  private breakpointObserver = inject(BreakpointObserver)

  private sessionKey = toSignal(this.route.paramMap.pipe(map(p => p.get('key') ?? '')))
  readonly playerId = computed(() => this.playerService.getPlayerId())
  readonly isMobile = toSignal(this.breakpointObserver.observe(Breakpoints.Handset).pipe(map(r => r.matches)), {initialValue: false})

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
  readonly participantsEnriched = computed(() => {
    const pid = this.playerId()
    return this.participants().map(p => ({
      participant: p,
      nation: this.nationById().get(this.infoByPlayerId().get(p.player.id)?.nation?.id ?? -1) ?? null,
      government: this.infoByPlayerId().get(p.player.id)?.government ?? null
    })).sort((a, b) => (b.participant.player.id === pid ? 1 : 0) - (a.participant.player.id === pid ? 1 : 0))
  })
  readonly opponentNation = computed(() => this.participantsEnriched().find(e => e.participant.player.id !== this.playerId())?.nation ?? null)
  readonly isHost = computed(() => this.workflow()?.host.id === this.playerId())
  readonly hasBattle = computed(() => !!this.workflow()?.activeBattle)
  readonly battle = computed(() => this.battleData())
  readonly battleStatus = computed(() => this.battle()?.status ?? null)
  readonly battleFinished = computed(() => this.battleStatus() === 'FINISHED' || this.battleStatus() === 'CANCELED')
  readonly activePlayerId = computed(() => this.battle()?.activePlayer.player.id ?? null)
  readonly isMyTurn = computed(() => this.activePlayerId() === this.playerId())
  readonly battleWinner = computed(() => this.battle()?.winner ?? null)
  readonly fronts = computed(() => this.battle()?.fronts ?? [])
  readonly selectedUnit = signal<GameUnit | null>(null)

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
    const mine = this.battle()?.myInfo
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
    interval(30000).pipe(
      startWith(0),
      switchMap(() => this.fetchSessionData()),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(data => {
      this.workflowData.set(data.workflow)
      this.participantsData.set(data.participants)
      this.battleData.set(data.battle)
    })

    const key = this.sessionKey()
    if (key) {
      this.workflowService.getSessionEvents(key).pipe(
        catchError(() => EMPTY),
        takeUntilDestroyed(this.destroyRef),
      ).subscribe(() => this.reloadAll())
    }
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

  startTour() { this.hasBattle() ? this.tourService.startBattleTour() : this.tourService.startLobbyTour() }
  openQrDialog() { this.dialog.open(SessionQrcodeDialogComponent, {data: {sessionId: this.sessionId(), qrUrl: this.qrUrl()}}) }
  copyLink() { navigator.clipboard.writeText(this.joinUrl()) }
  back() { this.router.navigate(['/home']) }
  reload() { this.reloadAll() }
  selectUnit(unit: GameUnit) { this.selectedUnit.set(this.selectedUnit()?.entity === unit.entity ? null : unit) }

  attackPlayer(defender: GameSessionPlayer) {
    const key = this.sessionKey()
    const attacker = this.myParticipant()
    if (!key || !attacker) return
    const participants = this.participants().map(p => p.player)
    this.dialog.open(SessionBattleStartDialogComponent, {data: {sessionKey: key, attacker: attacker.player, defender: defender.player, participants}, maxWidth: '95vw', width: '480px'})
      .afterClosed().subscribe(saved => { if (saved) this.reloadAll() })
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
      next: () => { this.battleData.set(null); this.reloadAll() },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  cancelBattle() {
    const key = this.sessionKey()
    if (!key) return
    this.workflowService.battleCancel(key).subscribe({
      next: () => {
        this.workflowService.getBattle(key).subscribe(battle => this.battleData.set(battle))
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
