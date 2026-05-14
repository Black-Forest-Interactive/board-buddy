import {Component, computed, inject, resource} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {UnitDefinitionService, WorkflowService} from '@board-buddy/admin'
import {Player, WorkflowCreateUnitRequest} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-session-create-unit-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './session-create-unit-dialog.component.html',
})
export class SessionCreateUnitDialogComponent {
  private workflowService = inject(WorkflowService)
  private unitDefinitionService = inject(UnitDefinitionService)
  private dialogRef = inject(MatDialogRef<SessionCreateUnitDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, participants: Player[]} = inject(MAT_DIALOG_DATA)
  readonly participants = this.data.participants

  private unitsResource = resource({
    loader: () => toPromise(this.unitDefinitionService.getUnitDefinitions(0, 100))
  })

  readonly unitDefinitions = computed(() => this.unitsResource.value()?.content ?? [])

  readonly form = new FormGroup({
    player: new FormControl<Player | null>(null, Validators.required),
    unitDefinition: new FormControl<any>(null, Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new WorkflowCreateUnitRequest(v.player!.id, v.unitDefinition!.id)
    this.workflowService.createUnit(this.data.sessionKey, request).subscribe({
      next: () => {
        this.translate.get('session.message.unitCreated').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
