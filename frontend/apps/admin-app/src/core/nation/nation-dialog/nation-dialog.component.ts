import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {NationService} from '@board-buddy/admin'
import {Nation, NationChangeRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-nation-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, TranslatePipe],
  templateUrl: './nation-dialog.component.html',
})
export class NationDialogComponent {
  private service = inject(NationService)
  private dialogRef = inject(MatDialogRef<NationDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly nation: Nation | null = inject(MAT_DIALOG_DATA, {optional: true})

  readonly form = new FormGroup({
    name: new FormControl(this.nation?.name ?? '', Validators.required),
    description: new FormControl(this.nation?.description ?? '', Validators.required),
    imageUrl: new FormControl(this.nation?.imageUrl ?? ''),
  })

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new NationChangeRequest(v.name!, v.description!, v.imageUrl ?? '')
    const call = this.nation ? this.service.update(this.nation.id, request) : this.service.create(request)
    call.subscribe({
      next: () => {
        const key = this.nation ? 'nation.message.updated' : 'nation.message.created'
        this.translate.get(key).subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('nation.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
