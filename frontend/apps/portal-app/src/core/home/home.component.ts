import {Component, computed, inject, resource} from '@angular/core'
import {Router} from '@angular/router'
import {MatButtonModule} from '@angular/material/button'
import {MatCardModule} from '@angular/material/card'
import {MatIconModule} from '@angular/material/icon'
import {MatDividerModule} from '@angular/material/divider'
import {TranslatePipe} from '@ngx-translate/core'
import {PlayerService, PortalSessionService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'portal-home',
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatDividerModule, TranslatePipe],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  private playerService = inject(PlayerService)
  private sessionService = inject(PortalSessionService)
  private router = inject(Router)

  private playerId = computed(() => this.playerService.getPlayerId())

  private playerResource = resource({
    params: this.playerId,
    loader: (p) => p.params ? toPromise(this.playerService.getPlayer(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private sessionsResource = resource({
    loader: (p) => toPromise(this.sessionService.getSessions(), p.abortSignal)
  })

  readonly playerName = computed(() => this.playerResource.value()?.name ?? '')
  readonly sessions = computed(() => this.sessionsResource.value() ?? [])

  createSession() { this.router.navigate(['/session', 'new']) }

  joinSession() { this.router.navigate(['/session', 'join']) }

  goToSession(key: string) { this.router.navigate(['/session', key]) }

  logout() {
    this.playerService.clearPlayerId()
    this.router.navigate(['/register'])
  }
}
