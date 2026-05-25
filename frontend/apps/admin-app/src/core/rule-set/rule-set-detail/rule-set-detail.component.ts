import {Component, computed, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute} from '@angular/router'
import {map} from 'rxjs'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {RuleSetService} from '@board-buddy/admin'
import {Technology, UnitDefinition} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {RuleSetAssignDialogComponent} from '../rule-set-assign-dialog/rule-set-assign-dialog.component'
import {RuleSetAssignTechnologyDialogComponent} from '../rule-set-assign-technology-dialog/rule-set-assign-technology-dialog.component'

@Component({
  selector: 'admin-rule-set-detail',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, TranslatePipe],
  templateUrl: './rule-set-detail.component.html',
})
export class RuleSetDetailComponent {
  private service = inject(RuleSetService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private id = toSignal(this.route.paramMap.pipe(map(p => Number(p.get('id')))))

  private ruleSetResource = resource({
    params: this.id,
    loader: (p) => p.params ? toPromise(this.service.getRuleSet(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  readonly ruleSet = computed(() => this.ruleSetResource.value())
  readonly name = computed(() => this.ruleSet()?.name ?? '')
  readonly unitDefinitions = computed(() => this.ruleSet()?.unitDefinitions ?? [])
  readonly technologies = computed(() => this.ruleSet()?.technologies ?? [])

  readonly unitColumns = ['name', 'unitType', 'counterType', 'maxLevel', 'actions']
  readonly technologyColumns = ['name', 'description', 'tier', 'actions']

  openAssignUnit() {
    const id = this.id()
    if (!id) return
    this.dialog.open(RuleSetAssignDialogComponent, {
      data: {ruleSetId: id, assigned: this.unitDefinitions()}
    }).afterClosed().subscribe(saved => { if (saved) this.ruleSetResource.reload() })
  }

  openAssignTechnology() {
    const id = this.id()
    if (!id) return
    this.dialog.open(RuleSetAssignTechnologyDialogComponent, {
      data: {ruleSetId: id, assigned: this.technologies()}
    }).afterClosed().subscribe(saved => { if (saved) this.ruleSetResource.reload() })
  }

  revokeUnit(unit: UnitDefinition) {
    const id = this.id()
    if (!id) return
    this.service.revokeUnitDefinition(id, unit.id).subscribe({
      next: (updated) => {
        this.ruleSetResource.set(updated)
        this.translate.get('rule-set.message.unitRevoked').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }

  revokeTechnology(tech: Technology) {
    const id = this.id()
    if (!id) return
    this.service.revokeTechnology(id, tech.id).subscribe({
      next: (updated) => {
        this.ruleSetResource.set(updated)
        this.translate.get('rule-set.message.technologyRevoked').subscribe(t => this.toast.success(t))
      },
      error: () => this.translate.get('rule-set.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
