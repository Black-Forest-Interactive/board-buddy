import {Component, computed, effect, inject} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatSelectModule} from '@angular/material/select'
import {MatCheckboxModule} from '@angular/material/checkbox'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {BattleParticipantRequest, BattleType, Player, PlayerType, WorkflowBattleStartRequest} from '@board-buddy/core'
import {PortalWorkflowService} from '@board-buddy/portal'
import {startWith} from 'rxjs'

@Component({
  selector: 'portal-session-battle-start-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatCheckboxModule, MatIconModule, MatButtonModule, TranslatePipe],
  templateUrl: './session-battle-start-dialog.component.html',
})
export class SessionBattleStartDialogComponent {
  private service = inject(PortalWorkflowService)
  private dialogRef = inject(MatDialogRef<SessionBattleStartDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, attacker: Player, defender: Player, participants: Player[]} = inject(MAT_DIALOG_DATA)

  readonly BattleType = BattleType
  readonly battleTypes = Object.values(BattleType)

  readonly form = new FormGroup({
    battleType: new FormControl<BattleType>(BattleType.ARMY_VS_ARMY, Validators.required),
    attackerArmyCount: new FormControl<number>(1, [Validators.required, Validators.min(1)]),
    defenderArmyCount: new FormControl<number>(1, [Validators.required, Validators.min(1)]),
    isWalled: new FormControl<boolean>(false),
  })

  private readonly battleType = toSignal(this.form.controls.battleType.valueChanges.pipe(startWith(BattleType.ARMY_VS_ARMY)))

  readonly isBarbarianBattle = computed(() => this.battleType() === BattleType.ARMY_VS_BARBARIANS)
  readonly isCityBattle = computed(() => this.battleType() === BattleType.ARMY_VS_CITY)

  readonly defender = computed(() => {
    if (this.isBarbarianBattle()) return this.data.participants.find(p => p.type === PlayerType.AI) ?? this.data.defender
    return this.data.defender
  })

  constructor() {
    effect(() => {
      if (this.isBarbarianBattle()) {
        this.form.controls.defenderArmyCount.setValue(1)
        this.form.controls.defenderArmyCount.disable()
      } else {
        this.form.controls.defenderArmyCount.enable()
      }
    })
  }

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const battleType = this.battleType() ?? BattleType.ARMY_VS_ARMY
    const defenderArmyCount = this.isBarbarianBattle() ? 1 : v.defenderArmyCount!
    const request = new WorkflowBattleStartRequest(
      new BattleParticipantRequest(this.data.attacker.id, v.attackerArmyCount!),
      new BattleParticipantRequest(this.defender().id, defenderArmyCount),
      battleType,
      this.isCityBattle() ? (v.isWalled ?? false) : false,
    )
    this.service.battleStart(this.data.sessionKey, request).subscribe({
      next: () => {
        this.translate.get('session.message.battleStarted').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
