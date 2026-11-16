import {ChangeDetectionStrategy, Component, computed, inject, resource} from '@angular/core'
import {DatePipe} from '@angular/common'
import {Router} from '@angular/router'
import {MatButtonModule} from '@angular/material/button'
import {MatCardModule} from '@angular/material/card'
import {MatIconModule} from '@angular/material/icon'
import {MatDividerModule} from '@angular/material/divider'
import {TranslatePipe} from '@ngx-translate/core'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MainContentComponent} from '@board-buddy/ui'
import {CurrentPlayerService, PortalSessionService, TourService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'portal-home',
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatDividerModule, MatTooltipModule, TranslatePipe, DatePipe, MainContentComponent],
  templateUrl: './home.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent {
  private playerService = inject(CurrentPlayerService)
  private sessionService = inject(PortalSessionService)
  private tourService = inject(TourService)
  private router = inject(Router)

  private sessionsResource = resource({
    loader: (p) => toPromise(this.sessionService.getSessions(), p.abortSignal)
  })

  readonly playerName = computed(() => this.playerService.player()?.name ?? '')
  readonly sessions = computed(() => this.sessionsResource.value() ?? [])

  startTour() { this.tourService.startHomeTour() }
  reload() { this.playerService.load(); this.sessionsResource.reload() }
  createSession() { this.router.navigate(['/session', 'new']) }
  joinSession() { this.router.navigate(['/session', 'join']) }
  goToSession(key: string) { this.router.navigate(['/session', key]) }

  logout() {
    this.playerService.logout().subscribe({
      complete: () => this.router.navigate(['/register'])
    })
  }
}
