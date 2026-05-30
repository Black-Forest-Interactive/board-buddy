import {Component, computed, inject, resource} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {Router, RouterModule} from '@angular/router'
import {TranslatePipe} from '@ngx-translate/core'
import {MainContentComponent} from '@board-buddy/ui'
import {PlayerService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

@Component({
  selector: 'portal-player',
  imports: [MatButtonModule, MatIconModule, MatCardModule, RouterModule, TranslatePipe, MainContentComponent],
  templateUrl: './player.component.html',
})
export class PlayerComponent {
  private playerService = inject(PlayerService)
  private router = inject(Router)

  private playerId = computed(() => this.playerService.getPlayerId())

  private playerResource = resource({
    params: this.playerId,
    loader: (p) => p.params ? toPromise(this.playerService.getPlayer(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  readonly playerName = computed(() => this.playerResource.value()?.name ?? '')
  readonly playerIdDisplay = computed(() => this.playerId() ?? '-')
  readonly loading = this.playerResource.isLoading

  back() { this.router.navigate(['/home']) }
  reload() { this.playerResource.reload() }
}
