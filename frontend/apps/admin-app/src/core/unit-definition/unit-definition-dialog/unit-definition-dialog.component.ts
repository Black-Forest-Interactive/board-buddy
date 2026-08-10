import {ChangeDetectionStrategy, Component, inject} from '@angular/core'
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatInputModule} from '@angular/material/input'
import {MatSelectModule} from '@angular/material/select'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {UnitDefinitionService} from '@board-buddy/admin'
import {PointsRange, UnitDefinition, UnitDefinitionChangeRequest, UnitType} from '@board-buddy/core'

@Component({
  selector: 'admin-unit-definition-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, TranslatePipe],
  templateUrl: './unit-definition-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnitDefinitionDialogComponent {
  private service = inject(UnitDefinitionService)
  private dialogRef = inject(MatDialogRef<UnitDefinitionDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly unit: UnitDefinition | null = inject(MAT_DIALOG_DATA, {optional: true})
  readonly unitTypes = Object.values(UnitType)

  readonly form = new FormGroup({
    name: new FormControl(this.unit?.name ?? '', Validators.required),
    unitType: new FormControl<UnitType>(this.unit?.unitType ?? UnitType.INFANTRY, Validators.required),
    counterType: new FormControl<UnitType | null>(this.unit?.counterType ?? null),
    damageMin: new FormControl(this.unit?.damagePoints.min ?? 1, [Validators.required, Validators.min(1)]),
    damageMax: new FormControl(this.unit?.damagePoints.max ?? 3, [Validators.required, Validators.min(1)]),
    healthMin: new FormControl(this.unit?.healthPoints.min ?? 1, [Validators.required, Validators.min(1)]),
    healthMax: new FormControl(this.unit?.healthPoints.max ?? 3, [Validators.required, Validators.min(1)]),
    maxLevel: new FormControl(this.unit?.maxLevel ?? 1, [Validators.required, Validators.min(1)]),
  })

  submit() {
    if (this.form.invalid) return
    const v = this.form.value
    const request = new UnitDefinitionChangeRequest(
      v.name!,
      v.unitType!,
      v.counterType ?? undefined,
      new PointsRange(v.damageMin!, v.damageMax!),
      new PointsRange(v.healthMin!, v.healthMax!),
      v.maxLevel!
    )
    const call = this.unit ? this.service.update(this.unit.id, request) : this.service.create(request)
    call.subscribe({
      next: () => {
        const key = this.unit ? 'unit-definition.message.updated' : 'unit-definition.message.created'
        this.translate.get(key).subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('unit-definition.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
