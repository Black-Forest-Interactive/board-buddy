import {ChangeDetectionStrategy, Component, computed, inject, resource, signal} from '@angular/core'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {UnitDefinitionService} from '@board-buddy/admin'
import {UnitDefinition} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {UnitDefinitionDialogComponent} from './unit-definition-dialog/unit-definition-dialog.component'

@Component({
  selector: 'admin-unit-definition',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './unit-definition.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnitDefinitionComponent {
  private service = inject(UnitDefinitionService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private unitDefinitionResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getUnitDefinitions(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.unitDefinitionResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.unitDefinitionResource.value()?.totalSize ?? 0)
  readonly loading = this.unitDefinitionResource.isLoading
  readonly error = this.unitDefinitionResource.error
  readonly columns = ['name', 'unitType', 'counterType', 'maxLevel', 'actions']

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openCreate() {
    this.dialog.open(UnitDefinitionDialogComponent)
      .afterClosed().subscribe(saved => {if (saved) this.unitDefinitionResource.reload()})
  }

  openEdit(unit: UnitDefinition) {
    this.dialog.open(UnitDefinitionDialogComponent, {data: unit})
      .afterClosed().subscribe(saved => {if (saved) this.unitDefinitionResource.reload()})
  }

  delete(unit: UnitDefinition) {
    this.service.remove(unit.id).subscribe({
      next: () => {
        this.translate.get('unit-definition.message.deleted').subscribe(t => this.toast.success(t))
        this.unitDefinitionResource.reload()
      },
      error: () => this.translate.get('unit-definition.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
