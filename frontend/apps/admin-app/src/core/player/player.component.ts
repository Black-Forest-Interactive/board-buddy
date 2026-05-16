import {Component, computed, inject, resource, signal} from '@angular/core'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PlayerService} from '@board-buddy/admin'
import {Player} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {PlayerDialogComponent} from './player-dialog/player-dialog.component'

@Component({
  selector: 'admin-player',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './player.component.html',
})
export class PlayerComponent {
  private service = inject(PlayerService)
  private dialog = inject(MatDialog)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private playerResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getPlayers(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.playerResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.playerResource.value()?.totalSize ?? 0)
  readonly loading = this.playerResource.isLoading
  readonly error = this.playerResource.error
  readonly columns = ['name', 'actions']

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openCreate() {
    this.dialog.open(PlayerDialogComponent)
      .afterClosed().subscribe(saved => {if (saved) this.playerResource.reload()})
  }

  openEdit(player: Player) {
    this.dialog.open(PlayerDialogComponent, {data: player})
      .afterClosed().subscribe(saved => {if (saved) this.playerResource.reload()})
  }

  delete(player: Player) {
    this.service.remove(player.id).subscribe({
      next: () => {
        this.translate.get('player.message.deleted').subscribe(t => this.toast.success(t))
        this.playerResource.reload()
      },
      error: () => this.translate.get('player.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
