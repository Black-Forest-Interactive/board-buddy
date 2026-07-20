import {ChangeDetectionStrategy, Component, computed, inject, resource} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatListModule} from '@angular/material/list'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {RuleSetService, TechnologyService} from '@board-buddy/admin'
import {Technology} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'admin-rule-set-assign-technology-dialog',
  imports: [MatDialogModule, MatListModule, MatButtonModule, MatIconModule, TranslatePipe],
  templateUrl: './rule-set-assign-technology-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RuleSetAssignTechnologyDialogComponent {
  private ruleSetService = inject(RuleSetService)
  private technologyService = inject(TechnologyService)
  private dialogRef = inject(MatDialogRef<RuleSetAssignTechnologyDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private data: {ruleSetId: number, assigned: Technology[]} = inject(MAT_DIALOG_DATA)

  private allResource = resource({
    loader: () => toPromise(this.technologyService.getTechnologies(0, 200))
  })

  readonly available = computed(() => {
    const assignedIds = new Set(this.data.assigned.map(t => t.id))
    return (this.allResource.value()?.content ?? []).filter(t => !assignedIds.has(t.id))
  })

  assign(tech: Technology) {
    this.ruleSetService.assignTechnology(this.data.ruleSetId, tech.id).subscribe({
      next: () => {
        this.translate.get('rule-set.message.technologyAssigned').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
