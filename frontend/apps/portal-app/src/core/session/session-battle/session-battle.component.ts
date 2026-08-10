import {ChangeDetectionStrategy, Component, computed, DestroyRef, inject, signal} from '@angular/core'
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute, Router} from '@angular/router'
import {catchError, EMPTY, interval, map, startWith, switchMap} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {BattleFront, BattleFrontUnit, GameUnit, Nation, Workflow, WorkflowBattleAttackFrontRequest, WorkflowBattleCreateFrontRequest, WorkflowParticipantInfo} from '@board-buddy/core'
import {MainContentComponent} from '@board-buddy/ui'
import {PlayerService, PortalBattle, PortalWorkflowService, TourService} from '@board-buddy/portal'

@Component({
  selector: 'portal-session-battle',
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatTooltipModule, TranslatePipe, MainContentComponent],
  templateUrl: './session-battle.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionBattleComponent {
  private workflowService = inject(PortalWorkflowService)
  private playerService = inject(PlayerService)
  private tourService = inject(TourService)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)
  private destroyRef = inject(DestroyRef)

  private sessionKey = toSignal(this.route.paramMap.pipe(map(p => p.get('key') ?? '')))
  readonly playerId = computed(() => this.playerService.getPlayerId())

  private readonly workflowData = signal<Workflow | undefined>(undefined)
  private readonly participantsData = signal<WorkflowParticipantInfo[]>([])
  private readonly battleData = signal<PortalBattle | undefined>(undefined)

  readonly workflow = computed(() => this.workflowData())
  readonly sessionName = computed(() => this.workflow()?.name ?? '')
  readonly isHost = computed(() => this.workflow()?.host.id === this.playerId())
  private nationById = computed(() => new Map<number, Nation>((this.workflow()?.ruleSet.nations ?? []).map(n => [n.id, n])))
  private infoByPlayerId = computed(() => new Map<number, WorkflowParticipantInfo>(this.participantsData().map(i => [i.player.id, i])))
  readonly opponentNation = computed(() => {
    const opponentId = this.battle()?.opponentInfo.player.player.id
    if (opponentId === undefined) return null
    return this.nationById().get(this.infoByPlayerId().get(opponentId)?.nation?.id ?? -1) ?? null
  })

  readonly battle = computed(() => this.battleData())
  readonly battleStatus = computed(() => this.battle()?.status ?? null)
  readonly battleFinished = computed(() => this.battleStatus() === 'FINISHED' || this.battleStatus() === 'CANCELED')
  readonly activePlayerId = computed(() => this.battle()?.activePlayer.player.id ?? null)
  readonly isMyTurn = computed(() => this.activePlayerId() === this.playerId())
  readonly battleWinner = computed(() => this.battle()?.winner ?? null)
  readonly fronts = computed(() => this.battle()?.fronts ?? [])
  readonly selectedUnit = signal<GameUnit | null>(null)

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
    interval(15000).pipe(
      startWith(0),
      switchMap(() => this.fetchBattleData()),
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
        if (e.type === 'BATTLE_FINISHED') this.router.navigate(['/session', key])
        else this.reload()
      })
    }
  }

  private fetchBattleData() {
    const key = this.sessionKey()
    if (!key) return EMPTY
    return this.workflowService.getWorkflow(key).pipe(
      switchMap(workflow => {
        if (!workflow.activeBattle) return EMPTY
        return this.workflowService.getParticipantsInfo(key).pipe(
          switchMap(participants => this.workflowService.getBattle(key).pipe(
            map(battle => ({workflow, participants, battle}))
          ))
        )
      })
    )
  }

  private reload() {
    this.fetchBattleData().subscribe({
      next: (data) => {
        this.workflowData.set(data.workflow)
        this.participantsData.set(data.participants)
        this.battleData.set(data.battle)
      },
      error: () => this.router.navigate(['/session', this.sessionKey()])
    })
  }

  startTour() { this.tourService.startBattleTour() }
  back() { this.router.navigate(['/session', this.sessionKey()]) }
  selectUnit(unit: GameUnit) { this.selectedUnit.set(this.selectedUnit()?.entity === unit.entity ? null : unit) }

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
      next: () => this.router.navigate(['/session', key]),
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  cancelBattle() {
    const key = this.sessionKey()
    if (!key) return
    this.workflowService.battleCancel(key).subscribe({
      next: () => this.workflowService.getBattle(key).subscribe(battle => this.battleData.set(battle)),
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
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

  getFrontUnitForMe(front: BattleFront): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id === this.playerId())
  }

  getFrontUnitForOpponent(front: BattleFront): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id !== this.playerId())
  }

  hasCounterAdvantage(front: BattleFront): boolean {
    const mine = this.getFrontUnitForMe(front)
    const opponent = this.getFrontUnitForOpponent(front)
    return !!mine && !!opponent && mine.unit.counterType?.kind === opponent.unit.type?.kind
  }
}
