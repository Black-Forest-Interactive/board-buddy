import {ChangeDetectionStrategy, Component, computed, DestroyRef, inject, resource, signal} from '@angular/core'
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop'
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout'
import {ActivatedRoute, Router, RouterModule} from '@angular/router'
import {catchError, combineLatest, EMPTY, interval, map, of, startWith, switchMap} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {MatDialog, MatDialogModule} from '@angular/material/dialog'
import {MatProgressBarModule} from '@angular/material/progress-bar'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameSessionPlayer, GameUnit, Nation, PlayerType, UnitDefinition, Workflow, WorkflowCreateUnitRequest, WorkflowParticipantInfo} from '@board-buddy/core'
import {MainContentComponent} from '@board-buddy/ui'
import {PlayerService, PortalBattle, PortalWorkflowService, TourService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'
import {SessionBattleStartDialogComponent} from '../session-battle-start-dialog/session-battle-start-dialog.component'
import {SessionContentComponent} from '../session-content/session-content.component'
import {SessionQrcodeDialogComponent} from '../session-qrcode-dialog/session-qrcode-dialog.component'

const UNIT_TYPE_ORDER: Record<string, number> = {INFANTRY: 0, MOUNTED: 1, ARTILLERY: 2, AIRCRAFT: 3}

@Component({
  selector: 'portal-session-lobby',
  imports: [RouterModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule, MatProgressBarModule, MatTooltipModule, TranslatePipe, MainContentComponent, SessionContentComponent],
  templateUrl: './session-lobby.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
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
  readonly myEnriched = computed(() => this.participantsEnriched().find(e => e.participant.player.id === this.playerId()))
  readonly opponentEnriched = computed(() => this.participantsEnriched().find(e => e.participant.player.id !== this.playerId()))
  readonly opponentNation = computed(() => this.opponentEnriched()?.nation ?? null)
  readonly opponentIsAi = computed(() => this.opponentEnriched()?.participant.player.type === PlayerType.AI)
  readonly isHost = computed(() => this.workflow()?.host.id === this.playerId())
  readonly hasBattle = computed(() => !!this.workflow()?.activeBattle)
  readonly battle = computed(() => this.battleData())
  readonly battleStatus = computed(() => this.battle()?.status ?? null)
  readonly battleFinished = computed(() => this.battleStatus() === 'FINISHED' || this.battleStatus() === 'CANCELED')
  readonly activePlayerId = computed(() => this.battle()?.activePlayer.player.id ?? null)
  readonly isMyTurn = computed(() => this.activePlayerId() === this.playerId())
  readonly fronts = computed(() => this.battle()?.fronts ?? [])
  // Backend doesn't expose a round counter — a battle alternates single-unit turns, so two log entries make up one round.
  readonly battleRound = computed(() => Math.floor((this.battle()?.logEntries.length ?? 0) / 2) + 1)
  readonly reserveCount = computed(() => {
    const mine = this.battle()?.myInfo
    if (!mine) return 0
    const onFront = new Set(this.fronts().flatMap(f => f.units.filter(fu => fu.player.player.id === this.playerId()).map(fu => fu.unit.entity)))
    return mine.units.filter(u => !onFront.has(u.entity)).length
  })

  readonly joinUrl = computed(() => {
    const key = this.sessionId()
    if (!key) return ''
    return `${window.location.origin}/session/join?key=${key}`
  })

  readonly qrUrl = computed(() => {
    const url = this.joinUrl()
    return url ? `https://api.qrserver.com/v1/create-qr-code/?size=256x256&data=${encodeURIComponent(url)}` : ''
  })

  private myInfoResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getMyInfo(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private technologyStatusResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getTechnologyStatus(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  readonly unitDefinitions = computed<UnitDefinition[]>(() => {
    const defs = this.workflow()?.ruleSet.unitDefinitions ?? []
    return [...defs].sort((a, b) => (UNIT_TYPE_ORDER[a.unitType] ?? 99) - (UNIT_TYPE_ORDER[b.unitType] ?? 99))
  })
  readonly myUnits = computed(() => this.myInfoResource.value()?.units ?? [])
  readonly unitLevel = computed(() => this.myInfoResource.value()?.unitLevel ?? {})
  readonly researchedCount = computed(() => this.technologyStatusResource.value()?.researched.length ?? 0)
  readonly totalTechCount = computed(() => {
    const s = this.technologyStatusResource.value()
    return s ? s.researched.length + s.available.length + s.blocked.length : 0
  })
  // "Zeitalter" isn't tracked as its own field — it's the furthest tier the player has researched into.
  readonly age = computed(() => {
    const tiers = (this.technologyStatusResource.value()?.researched ?? []).map(t => t.tier)
    return tiers.length ? Math.max(...tiers) : 1
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
      ).subscribe(e => {
        if (e.type === 'BATTLE_STARTED') { this.router.navigate(['/session', key, 'battle']); return }
        this.reloadAll()
        this.myInfoResource.reload()
        this.technologyStatusResource.reload()
      })
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
  reload() { this.reloadAll(); this.myInfoResource.reload(); this.technologyStatusResource.reload() }

  unitsOfType(unitType: string): GameUnit[] {
    return this.myUnits().filter(u => u.type?.kind === unitType)
  }

  unitImagePath(kind: string | null | undefined): string | null {
    if (!kind) return null
    const map: Record<string, string> = {INFANTRY: '/img/unit/infantry2_mini.jpg', MOUNTED: '/img/unit/cavalry2_mini.jpg', ARTILLERY: '/img/unit/artillery2_mini.jpg', AIRCRAFT: '/img/unit/plane2_mini.jpg'}
    return map[kind] ?? null
  }

  createUnit(unitDef: UnitDefinition) {
    const key = this.sessionKey()
    const pid = this.playerId()
    if (!key || !pid) return
    this.workflowService.createUnit(key, new WorkflowCreateUnitRequest(pid, unitDef.id)).subscribe({
      next: () => {
        this.translate.get('session.message.unitCreated').subscribe(t => this.toast.success(t))
        this.myInfoResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  attackPlayer(defender: GameSessionPlayer) {
    const key = this.sessionKey()
    const attacker = this.myParticipant()
    if (!key || !attacker) return
    const participants = this.participants().map(p => p.player)
    this.dialog.open(SessionBattleStartDialogComponent, {data: {sessionKey: key, attacker: attacker.player, defender: defender.player, participants}, maxWidth: '95vw', width: '480px'})
      .afterClosed().subscribe(saved => { if (saved) this.router.navigate(['/session', key, 'battle']) })
  }
}
