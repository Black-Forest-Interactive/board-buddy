import {ChangeDetectionStrategy, Component, computed, inject, resource, signal} from '@angular/core'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {GameService} from '@board-buddy/admin'
import {Game} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {Router} from '@angular/router'
import {GameDialogComponent} from './game-dialog/game-dialog.component'

@Component({
  selector: 'admin-game',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './game.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameComponent {
  private service = inject(GameService)
  private dialog = inject(MatDialog)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private gameResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getGames(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.gameResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.gameResource.value()?.totalSize ?? 0)
  readonly loading = this.gameResource.isLoading
  readonly error = this.gameResource.error
  readonly columns = ['name', 'description', 'ruleSets', 'actions']

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openDetail(game: Game) {
    this.router.navigate(['/game', game.id])
  }

  openCreate() {
    this.dialog.open(GameDialogComponent)
      .afterClosed().subscribe(saved => {if (saved) this.gameResource.reload()})
  }

  openEdit(game: Game) {
    this.dialog.open(GameDialogComponent, {data: game})
      .afterClosed().subscribe(saved => {if (saved) this.gameResource.reload()})
  }

  delete(game: Game) {
    this.service.remove(game.id).subscribe({
      next: () => {
        this.translate.get('game.message.deleted').subscribe(t => this.toast.success(t))
        this.gameResource.reload()
      },
      error: () => this.translate.get('game.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
