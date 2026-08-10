import {ChangeDetectionStrategy, Component, computed, effect, inject} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatCheckboxModule} from '@angular/material/checkbox'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {WorkflowService} from '@board-buddy/admin'
import {BattleParticipantRequest, BattleType, Player, WorkflowBattleStartRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-session-battle-start-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './session-battle-start-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionBattleStartDialogComponent {
  private service = inject(WorkflowService)
  private dialogRef = inject(MatDialogRef<SessionBattleStartDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, participants: Player[]} = inject(MAT_DIALOG_DATA)
  readonly participants = this.data.participants
  readonly battleTypes = Object.values(BattleType)

  readonly form = new FormGroup({
    attacker: new FormControl<Player | null>(null, Validators.required),
    attackerArmyCount: new FormControl<number>(1, [Validators.required, Validators.min(1)]),
    defender: new FormControl<Player | null>(null, Validators.required),
    defenderArmyCount: new FormControl<number>(1, [Validators.required, Validators.min(1)]),
    type: new FormControl<BattleType>(BattleType.ARMY_VS_ARMY, Validators.required),
    isWalled: new FormControl<boolean>(false),
  })

  private selectedAttacker = toSignal(this.form.controls.attacker.valueChanges, {initialValue: null})

  readonly defenders = computed(() =>
    this.participants.filter(p => p.id !== this.selectedAttacker()?.id)
  )

  constructor() {
    effect(() => {
      const available = this.defenders()
      if (available.length === 1) this.form.controls.defender.setValue(available[0])
    })
  }

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new WorkflowBattleStartRequest(
      new BattleParticipantRequest(v.attacker!.id, v.attackerArmyCount!),
      new BattleParticipantRequest(v.defender!.id, v.defenderArmyCount!),
      v.type!,
      v.isWalled ?? false,
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
