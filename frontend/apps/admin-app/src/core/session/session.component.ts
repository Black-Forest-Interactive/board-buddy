import {Component, computed, inject, resource, signal} from '@angular/core'
import {MatTableModule} from '@angular/material/table'
import {MatButtonModule} from '@angular/material/button'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatIconModule} from '@angular/material/icon'
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator'
import {MatDialog} from '@angular/material/dialog'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {SessionService} from '@board-buddy/admin'
import {GameSession} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'
import {MainContentComponent} from '@board-buddy/ui'
import {Router} from '@angular/router'
import {SessionDialogComponent} from './session-dialog/session-dialog.component'

@Component({
  selector: 'admin-session',
  imports: [MainContentComponent, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatPaginatorModule, TranslatePipe],
  templateUrl: './session.component.html',
})
export class SessionComponent {
  private service = inject(SessionService)
  private dialog = inject(MatDialog)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  private page = signal(0)
  private size = signal(20)
  private criteria = computed(() => ({page: this.page(), size: this.size()}))

  private sessionResource = resource({
    params: this.criteria,
    loader: (p) => toPromise(this.service.getSessions(p.params.page, p.params.size), p.abortSignal)
  })

  readonly items = computed(() => this.sessionResource.value()?.content ?? [])
  readonly totalSize = computed(() => this.sessionResource.value()?.totalSize ?? 0)
  readonly loading = this.sessionResource.isLoading
  readonly error = this.sessionResource.error
  readonly columns = ['name', 'host', 'game', 'participants', 'actions']

  handlePageChange(event: PageEvent) {
    this.page.set(event.pageIndex)
    this.size.set(event.pageSize)
  }

  openDetail(session: GameSession) {
    this.router.navigate(['/session', session.id])
  }

  openCreate() {
    this.dialog.open(SessionDialogComponent)
      .afterClosed().subscribe(saved => {if (saved) this.sessionResource.reload()})
  }

  delete(session: GameSession) {
    this.service.remove(session.id).subscribe({
      next: () => {
        this.translate.get('session.message.deleted').subscribe(t => this.toast.success(t))
        this.sessionResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
