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
  BattleInfo,
  BattleParticipantInfo,
  GameUnit,
  Player,
  UnitDefinition,
  WorkflowBattleCreateFrontRequest,
  WorkflowBattleStartRequest,
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
      ? toPromise(this.workflowService.getBattleInfo(p.params), p.abortSignal).catch(() => null)
      : Promise.resolve(null)
  })

  readonly session = computed(() => this.sessionResource.value())
  readonly sessionKey = computed(() => this.session()?.key ?? '')
  readonly name = computed(() => this.session()?.name ?? '')
  readonly participants = computed(() => this.session()?.participants ?? [])
  readonly unitDefinitions = computed(() => this.session()?.ruleSet.unitDefinitions ?? [])
  readonly participantsInfo = computed(() => this.participantsInfoResource.value() ?? [])
  readonly battleInfo = computed(() => this.battleInfoResource.value() as BattleInfo | null)
  readonly hasBattle = computed(() => this.battleInfo() != null)
  readonly activePlayerId = computed(() => this.battleInfo()?.activePlayer.id ?? null)

  openAssign() {
    const id = this.id()
    if (!id) return
    this.dialog.open(SessionAssignDialogComponent, {
      data: {sessionId: id, participants: this.participants()}
    }).afterClosed().subscribe(saved => {if (saved) this.sessionResource.reload()})
  }

  createUnit(player: Player, unitDef: UnitDefinition) {
    const key = this.sessionKey()
    if (!key) return
    const request = new WorkflowCreateUnitRequest(player.id, unitDef.id)
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
      data: {sessionKey: key, participants: this.participants()}
    }).afterClosed().subscribe(saved => {if (saved) this.battleInfoResource.reload()})
  }

  createFront(participant: BattleParticipantInfo, unit: GameUnit) {
    const key = this.sessionKey()
    if (!key) return
    this.workflowService.battleCreateFront(key, new WorkflowBattleCreateFrontRequest(participant.player.id, unit.entity))
      .subscribe({
        next: (info) => this.battleInfoResource.set(info),
        error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
      })
  }

  openAttack(attacker: BattleParticipantInfo, defender: BattleParticipantInfo, frontIndex: number) {
    const key = this.sessionKey()
    if (!key) return
    this.dialog.open(SessionAttackDialogComponent, {
      data: {sessionKey: key, attacker, defender, frontIndex}
    }).afterClosed().subscribe(info => {if (info) this.battleInfoResource.set(info)})
  }

  isActive(participant: BattleParticipantInfo): boolean {
    return participant.player.id === this.activePlayerId()
  }

  isOnFront(participant: BattleParticipantInfo, unit: GameUnit): boolean {
    return participant.fronts.some(f => f.unit.entity === unit.entity)
  }

  opponentOf(participant: BattleParticipantInfo): BattleParticipantInfo | undefined {
    return this.battleInfo()?.participant.find(p => p.player.id !== participant.player.id)
  }

  revokePlayer(player: Player) {
    const id = this.id()
    if (!id) return
    this.sessionService.revokePlayer(id, player.id).subscribe({
      next: (updated) => {
        this.sessionResource.set(updated)
        this.translate.get('session.message.playerRevoked').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
