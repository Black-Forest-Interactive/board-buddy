import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {NationService} from '@board-buddy/admin'
import {GovernmentType, NationEffectInitialGovernmentRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-nation-effect-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './nation-effect-dialog.component.html',
})
export class NationEffectDialogComponent {
  private service = inject(NationService)
  private dialogRef = inject(MatDialogRef<NationEffectDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly nationId: number = inject(MAT_DIALOG_DATA)

  readonly governmentTypes = Object.values(GovernmentType)

  readonly form = new FormGroup({
    type: new FormControl<GovernmentType | null>(null, Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const request = new NationEffectInitialGovernmentRequest(this.form.value.type!)
    this.service.assignInitialGovernment(this.nationId, request).subscribe({
      next: (nation) => {
        this.translate.get('nation.effect.message.added').subscribe(t => this.toast.success(t))
        this.dialogRef.close(nation)
      },
      error: () => this.translate.get('nation.effect.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
