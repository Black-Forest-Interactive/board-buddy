import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatCheckboxModule} from '@angular/material/checkbox'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {BattleParticipantRequest, BattleType, Player, WorkflowBattleStartRequest} from '@board-buddy/core'
import {PortalWorkflowService} from '@board-buddy/portal'

@Component({
  selector: 'portal-session-battle-start-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatIconModule, MatButtonModule, TranslatePipe],
  templateUrl: './session-battle-start-dialog.component.html',
})
export class SessionBattleStartDialogComponent {
  private service = inject(PortalWorkflowService)
  private dialogRef = inject(MatDialogRef<SessionBattleStartDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, attacker: Player, defender: Player} = inject(MAT_DIALOG_DATA)

  readonly form = new FormGroup({
    attackerArmyCount: new FormControl<number>(1, [Validators.required, Validators.min(1)]),
    defenderArmyCount: new FormControl<number>(1, [Validators.required, Validators.min(1)]),
    isWalled: new FormControl<boolean>(false),
  })

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new WorkflowBattleStartRequest(
      new BattleParticipantRequest(this.data.attacker.id, v.attackerArmyCount!),
      new BattleParticipantRequest(this.data.defender.id, v.defenderArmyCount!),
      BattleType.ARMY_VS_ARMY,
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
