import {ChangeDetectionStrategy, Component, computed, inject, resource} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatListModule} from '@angular/material/list'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {RuleSetService, UnitDefinitionService} from '@board-buddy/admin'
import {UnitDefinition} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-rule-set-assign-dialog',
  imports: [MatDialogModule, MatListModule, MatButtonModule, MatIconModule, TranslatePipe],
  templateUrl: './rule-set-assign-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RuleSetAssignDialogComponent {
  private ruleSetService = inject(RuleSetService)
  private unitDefinitionService = inject(UnitDefinitionService)
  private dialogRef = inject(MatDialogRef<RuleSetAssignDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private data: {ruleSetId: number, assigned: UnitDefinition[]} = inject(MAT_DIALOG_DATA)

  private allResource = resource({
    loader: () => toPromise(this.unitDefinitionService.getUnitDefinitions(0, 100))
  })

  readonly available = computed(() => {
    const assignedIds = new Set(this.data.assigned.map(u => u.id))
    return (this.allResource.value()?.content ?? []).filter(u => !assignedIds.has(u.id))
  })

  assign(unit: UnitDefinition) {
    this.ruleSetService.assignUnitDefinition(this.data.ruleSetId, unit.id).subscribe({
      next: () => {
        this.translate.get('rule-set.message.unitAssigned').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
