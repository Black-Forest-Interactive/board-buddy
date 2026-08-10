import {ChangeDetectionStrategy, Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {RuleSetService} from '@board-buddy/admin'
import {RuleSet, RuleSetChangeRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-rule-set-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    TranslatePipe,
  ],
  templateUrl: './rule-set-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RuleSetDialogComponent {
  private service = inject(RuleSetService)
  private dialogRef = inject(MatDialogRef<RuleSetDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly ruleSet: RuleSet | null = inject(MAT_DIALOG_DATA, {optional: true})

  readonly form = new FormGroup({
    name: new FormControl(this.ruleSet?.name ?? '', Validators.required),
  })

  submit() {
    if (this.form.invalid) return
    const request = new RuleSetChangeRequest(this.form.controls.name.value!)
    const call = this.ruleSet
      ? this.service.update(this.ruleSet.id, request)
      : this.service.create(request)

    call.subscribe({
      next: () => {
        const key = this.ruleSet ? 'rule-set.message.updated' : 'rule-set.message.created'
        this.translate.get(key).subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
