import {Component, computed, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute} from '@angular/router'
import {map} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatCardModule} from '@angular/material/card'
import {MatChipsModule} from '@angular/material/chips'
import {MatDividerModule} from '@angular/material/divider'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {SessionService, WorkflowService} from '@board-buddy/admin'
import {
  Battle,
  BattleFront,
  BattleFrontUnit,
  BattleParticipant,
  GameSessionPlayer,
  GameUnit,
  UnitDefinition,
  WorkflowBattleCreateFrontRequest,
  WorkflowCreateUnitRequest,
  WorkflowParticipantInfo,
} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {SessionAssignDialogComponent} from '../session-assign-dialog/session-assign-dialog.component'
import {SessionBattleStartDialogComponent} from '../session-battle-start-dialog/session-battle-start-dialog.component'
import {SessionAttackDialogComponent} from '../session-attack-dialog/session-attack-dialog.component'

@Component({
  selector: 'admin-session-detail',
  imports: [
    MainContentComponent,
    MatButtonModule, MatIconModule, MatTooltipModule,
    MatCardModule, MatChipsModule, MatDividerModule,
    TranslatePipe,
  ],
  templateUrl: './session-detail.component.html',
  styleUrl: './session-detail.component.scss',
})
export class SessionDetailComponent {
  private sessionService = inject(SessionService)
  private workflowService = inject(WorkflowService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private id = toSignal(this.route.paramMap.pipe(map(p => Number(p.get('id')))))

  private sessionResource = resource({
    params: this.id,
    loader: (p) => p.params ? toPromise(this.sessionService.getSession(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private participantsInfoResource = resource({
    params: computed(() => this.sessionResource.value()?.key),
    loader: (p) => p.params
      ? toPromise(this.workflowService.getParticipantsInfo(p.params), p.abortSignal)
      : Promise.resolve([] as WorkflowParticipantInfo[])
  })

  private battleInfoResource = resource({
    params: computed(() => this.sessionResource.value()?.key),
    loader: (p) => p.params
      ? toPromise(this.workflowService.getBattleInfo(p.params), p.abortSignal).catch(() => null as Battle | null)
      : Promise.resolve(null as Battle | null)
  })

  readonly session = computed(() => this.sessionResource.value())
  readonly sessionKey = computed(() => this.session()?.key ?? '')
  readonly name = computed(() => this.session()?.name ?? '')
  readonly participants = computed(() => this.session()?.participants ?? [])
  readonly unitDefinitions = computed(() => this.session()?.ruleSet.unitDefinitions ?? [])
  readonly participantsInfo = computed(() => this.participantsInfoResource.value() ?? [])
  readonly battleInfo = computed(() => this.battleInfoResource.value() ?? null)
  readonly hasBattle = computed(() => this.battleInfo() != null)
  readonly activePlayerId = computed(() => this.battleInfo()?.activePlayer.player.id ?? null)
  readonly battleAttacker = computed(() => this.battleInfo()?.participant[0] ?? null)
  readonly battleDefender = computed(() => this.battleInfo()?.participant[1] ?? null)
  readonly battleWinner = computed(() => this.battleInfo()?.winner ?? null)
  readonly battleFinished = computed(() => this.battleInfo()?.status === 'FINISHED')
  readonly sharedFronts = computed(() => this.battleInfo()?.fronts ?? [])
  readonly logEntries = computed(() => this.battleInfo()?.logEntries ?? [])
  readonly unitLookup = computed(() => {
    const battle = this.battleInfo()
    if (!battle) return new Map<number, GameUnit>()
    const map = new Map<number, GameUnit>()
    for (const p of battle.participant) for (const u of p.units) map.set(u.entity, u)
    for (const f of battle.fronts) for (const fu of f.units) map.set(fu.unit.entity, fu.unit)
    return map
  })
  readonly attackableFronts = computed(() => {
    const battle = this.battleInfo()
    if (!battle) return new Set<number>()
    const activeId = battle.activePlayer.player.id
    const attacker = battle.participant.find(p => p.player.player.id === activeId)
    if (!attacker) return new Set<number>()
    const onFront = new Set(battle.fronts.flatMap(f => f.units.filter(fu => fu.player.player.id === activeId).map(fu => fu.unit.entity)))
    if (!attacker.units.some(u => !onFront.has(u.entity))) return new Set<number>()
    return new Set(battle.fronts.filter(f =>
      f.units.some(fu => fu.player.player.id !== activeId) && !f.units.some(fu => fu.player.player.id === activeId)
    ).map(f => f.index))
  })

  finishBattle() {
    const key = this.sessionKey()
    if (!key) return
    this.workflowService.battleFinish(key).subscribe({
      next: () => {
        this.battleInfoResource.set(null)
        this.participantsInfoResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  openAssign() {
    const key = this.sessionKey()
    if (!key) return
    this.dialog.open(SessionAssignDialogComponent, {
      data: {sessionKey: key, participants: this.participants().map(p => p.player)}
    }).afterClosed().subscribe(saved => {
      if (saved) {
        this.sessionResource.reload()
        this.participantsInfoResource.reload()
      }
    })
  }

  createUnit(player: GameSessionPlayer, unitDef: UnitDefinition) {
    const key = this.sessionKey()
    if (!key) return
    const request = new WorkflowCreateUnitRequest(player.player.id, unitDef.id)
    this.workflowService.createUnit(key, request).subscribe({
      next: () => {
        this.translate.get('session.message.unitCreated').subscribe(t => this.toast.success(t))
        this.participantsInfoResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }

  openBattleStart() {
    const key = this.sessionKey()
    if (!key) return
    this.dialog.open(SessionBattleStartDialogComponent, {
      data: {sessionKey: key, participants: this.participants().map(p => p.player)}
    }).afterClosed().subscribe(saved => {if (saved) this.battleInfoResource.reload()})
  }

  createFront(participant: BattleParticipant, unit: GameUnit) {
    const key = this.sessionKey()
    if (!key) return
    this.workflowService.battleCreateFront(key, new WorkflowBattleCreateFrontRequest(participant.player.player.id, unit.entity))
      .subscribe({
        next: (workflow) => this.battleInfoResource.set(workflow.activeBattle),
        error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
      })
  }

  openAttack(frontIndex: number) {
    const key = this.sessionKey()
    if (!key) return
    const battle = this.battleInfo()
    if (!battle) return
    const attackerId = battle.activePlayer.player.id
    const defender = battle.participant.find(p => p.player.player.id !== attackerId)
    if (!defender) return
    const attacker = battle.participant.find(p => p.player.player.id === attackerId)
    const onFront = new Set(battle.fronts.flatMap(f => f.units.filter(fu => fu.player.player.id === attackerId).map(fu => fu.unit.entity)))
    const availableUnits = attacker?.units.filter(u => !onFront.has(u.entity)) ?? []
    this.dialog.open(SessionAttackDialogComponent, {
      data: {sessionKey: key, attackerId, defenderId: defender.player.player.id, frontIndex, availableUnits}
    }).afterClosed().subscribe((result: Battle | undefined) => {if (result) this.battleInfoResource.set(result)})
  }

  isActive(participant: BattleParticipant): boolean {
    return participant.player.player.id === this.activePlayerId()
  }

  getUnitName(entityId: number): string {
    const unit = this.unitLookup().get(entityId)
    return unit?.type?.kind ?? `#${entityId}`
  }

  unitImagePath(kind: string | null | undefined): string | null {
    if (!kind) return null
    const map: Record<string, string> = {'INFANTRY': '/img/infantry.jpg', 'CAVALRY': '/img/cavalry.jpg', 'ARTILLERY': '/img/artillery.jpg', 'PLANE': '/img/plane.jpg'}
    return map[kind] ?? null
  }

  getFrontUnitForPlayer(front: BattleFront, participant: BattleParticipant): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id === participant.player.player.id)
  }

  reserveUnits(participant: BattleParticipant): GameUnit[] {
    const onFront = new Set(this.sharedFronts().flatMap(f => f.units.filter(fu => fu.player.player.id === participant.player.player.id).map(fu => fu.unit.entity)))
    return participant.units.filter(u => !onFront.has(u.entity))
  }

  revokePlayer(player: GameSessionPlayer) {
    const id = this.id()
    if (!id) return
    this.sessionService.revokePlayer(id, player.player.id).subscribe({
      next: (updated) => {
        this.sessionResource.set(updated)
        this.translate.get('session.message.playerRevoked').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
