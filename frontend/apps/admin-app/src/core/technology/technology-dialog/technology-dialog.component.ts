import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {TechnologyService} from '@board-buddy/admin'
import {Technology, TechnologyChangeRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-technology-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, TranslatePipe],
  templateUrl: './technology-dialog.component.html',
})
export class TechnologyDialogComponent {
  private service = inject(TechnologyService)
  private dialogRef = inject(MatDialogRef<TechnologyDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly tech: Technology | null = inject(MAT_DIALOG_DATA, {optional: true})

  readonly form = new FormGroup({
    name: new FormControl(this.tech?.name ?? '', Validators.required),
    description: new FormControl(this.tech?.description ?? '', Validators.required),
    imageUrl: new FormControl(this.tech?.imageUrl ?? ''),
    tier: new FormControl(this.tech?.tier ?? 1, [Validators.required, Validators.min(1), Validators.max(5)]),
  })

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new TechnologyChangeRequest(v.name!, v.description!, v.imageUrl ?? '', v.tier!)
    const call = this.tech ? this.service.update(this.tech.id, request) : this.service.create(request)
    call.subscribe({
      next: () => {
        const key = this.tech ? 'technology.message.updated' : 'technology.message.created'
        this.translate.get(key).subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('technology.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
