import {ChangeDetectionStrategy, Component, computed, inject, signal} from '@angular/core'
import {DatePipe} from '@angular/common'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {Router, RouterModule} from '@angular/router'
import {TranslatePipe} from '@ngx-translate/core'
import {MainContentComponent} from '@board-buddy/ui'
import {CurrentPlayerService} from '@board-buddy/portal'

@Component({
  selector: 'portal-player',
  imports: [MatButtonModule, MatIconModule, MatCardModule, RouterModule, TranslatePipe, DatePipe, MainContentComponent],
  templateUrl: './player.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlayerComponent {
  private playerService = inject(CurrentPlayerService)
  private router = inject(Router)

  private readonly reloading = signal(false)

  readonly playerName = computed(() => this.playerService.player()?.name ?? '')
  readonly playerIdDisplay = computed(() => this.playerService.player()?.id ?? '-')
  readonly playerTimestamp = computed(() => this.playerService.player()?.timestamp ?? '')
  readonly loading = this.reloading.asReadonly()

  back() { this.router.navigate(['/home']) }
  reload() {
    this.reloading.set(true)
    this.playerService.load().finally(() => this.reloading.set(false))
  }
}
