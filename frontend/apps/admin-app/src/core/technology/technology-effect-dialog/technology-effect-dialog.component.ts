import {Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {TechnologyService} from '@board-buddy/admin'
import {UnitType, TechnologyEffectUnitUnlockRequest} from '@board-buddy/core'

@Component({
  selector: 'admin-technology-effect-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './technology-effect-dialog.component.html',
})
export class TechnologyEffectDialogComponent {
  private service = inject(TechnologyService)
  private dialogRef = inject(MatDialogRef<TechnologyEffectDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly technologyId: number = inject(MAT_DIALOG_DATA)

  readonly unitTypes = Object.values(UnitType)

  readonly form = new FormGroup({
    unitType: new FormControl<UnitType | null>(null, Validators.required),
    unitLevel: new FormControl<number>(1, [Validators.required, Validators.min(1), Validators.max(10)]),
  })

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new TechnologyEffectUnitUnlockRequest(v.unitType!, v.unitLevel!)
    this.service.assignUnitUnlock(this.technologyId, request).subscribe({
      next: (tech) => {
        this.translate.get('technology.effect.message.added').subscribe(t => this.toast.success(t))
        this.dialogRef.close(tech)
      },
      error: () => this.translate.get('technology.effect.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
