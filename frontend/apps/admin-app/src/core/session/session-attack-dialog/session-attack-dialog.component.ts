import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {WorkflowService} from '@board-buddy/admin'
import {
  BattleInfo,
  BattleParticipantInfo,
  GameUnit,
  WorkflowBattleAttackFrontRequest,
} from '@board-buddy/core'

@Component({
  selector: 'admin-session-attack-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './session-attack-dialog.component.html',
})
export class SessionAttackDialogComponent {
  private service = inject(WorkflowService)
  private dialogRef = inject(MatDialogRef<SessionAttackDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {
    sessionKey: string
    attacker: BattleParticipantInfo
    defender: BattleParticipantInfo
    frontIndex: number
  } = inject(MAT_DIALOG_DATA)

  readonly availableUnits = this.data.attacker.units.filter(u =>
    !this.data.attacker.fronts.some(f => f.unit.entity === u.entity)
  )

  readonly form = new FormGroup({
    unit: new FormControl<GameUnit | null>(null, Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const request = new WorkflowBattleAttackFrontRequest(
      this.data.attacker.player.id,
      this.data.defender.player.id,
      this.form.value.unit!.entity,
      this.data.frontIndex,
    )
    this.service.battleAttackFront(this.data.sessionKey, request).subscribe({
      next: (info: BattleInfo) => {
        this.translate.get('session.battle.attacked').subscribe(t => this.toast.success(t))
        this.dialogRef.close(info)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
