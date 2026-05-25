import {Component, computed, inject, resource, signal} from '@angular/core'
import {RouterModule} from '@angular/router'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {TechnologyService} from '@board-buddy/admin'
import {Technology} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {TechnologyDialogComponent} from './technology-dialog/technology-dialog.component'

@Component({
  selector: 'admin-technology',
  imports: [MainContentComponent, RouterModule, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './technology.component.html',
})
export class TechnologyComponent {
  private service = inject(TechnologyService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private technologyResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getTechnologies(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.technologyResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.technologyResource.value()?.totalSize ?? 0)
  readonly loading = this.technologyResource.isLoading
  readonly columns = ['name', 'description', 'tier', 'effects', 'actions']

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openCreate() {
    this.dialog.open(TechnologyDialogComponent)
      .afterClosed().subscribe(saved => { if (saved) this.technologyResource.reload() })
  }

  openEdit(tech: Technology) {
    this.dialog.open(TechnologyDialogComponent, {data: tech})
      .afterClosed().subscribe(saved => { if (saved) this.technologyResource.reload() })
  }

  delete(tech: Technology) {
    this.service.remove(tech.id).subscribe({
      next: () => {
        this.translate.get('technology.message.deleted').subscribe(t => this.toast.success(t))
        this.technologyResource.reload()
      },
      error: () => this.translate.get('technology.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
