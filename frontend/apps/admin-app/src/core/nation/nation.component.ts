import {ChangeDetectionStrategy, Component, computed, inject, resource, signal} from '@angular/core'
import {RouterModule} from '@angular/router'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {NationService} from '@board-buddy/admin'
import {Nation} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {NationDialogComponent} from './nation-dialog/nation-dialog.component'

@Component({
  selector: 'admin-nation',
  imports: [MainContentComponent, RouterModule, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './nation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NationComponent {
  private service = inject(NationService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private nationResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getNations(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.nationResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.nationResource.value()?.totalSize ?? 0)
  readonly loading = this.nationResource.isLoading
  readonly columns = ['image', 'name', 'description', 'effects', 'actions']

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openCreate() {
    this.dialog.open(NationDialogComponent)
      .afterClosed().subscribe(saved => { if (saved) this.nationResource.reload() })
  }

  openEdit(nation: Nation) {
    this.dialog.open(NationDialogComponent, {data: nation})
      .afterClosed().subscribe(saved => { if (saved) this.nationResource.reload() })
  }

  delete(nation: Nation) {
    this.service.remove(nation.id).subscribe({
      next: () => {
        this.translate.get('nation.message.deleted').subscribe(t => this.toast.success(t))
        this.nationResource.reload()
      },
      error: () => this.translate.get('nation.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
