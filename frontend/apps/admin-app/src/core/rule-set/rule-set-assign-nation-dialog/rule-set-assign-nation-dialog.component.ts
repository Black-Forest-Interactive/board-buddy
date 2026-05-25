import {Component, computed, inject, resource} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatListModule} from '@angular/material/list'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {NationService, RuleSetService} from '@board-buddy/admin'
import {Nation} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-rule-set-assign-nation-dialog',
  imports: [MatDialogModule, MatListModule, MatButtonModule, MatIconModule, TranslatePipe],
  templateUrl: './rule-set-assign-nation-dialog.component.html',
})
export class RuleSetAssignNationDialogComponent {
  private ruleSetService = inject(RuleSetService)
  private nationService = inject(NationService)
  private dialogRef = inject(MatDialogRef<RuleSetAssignNationDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private data: {ruleSetId: number, assigned: Nation[]} = inject(MAT_DIALOG_DATA)

  private allResource = resource({
    loader: () => toPromise(this.nationService.getNations(0, 200))
  })

  readonly available = computed(() => {
    const assignedIds = new Set(this.data.assigned.map(n => n.id))
    return (this.allResource.value()?.content ?? []).filter(n => !assignedIds.has(n.id))
  })

  assign(nation: Nation) {
    this.ruleSetService.assignNation(this.data.ruleSetId, nation.id).subscribe({
      next: () => {
        this.translate.get('rule-set.message.nationAssigned').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
