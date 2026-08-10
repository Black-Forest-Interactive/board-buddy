import {ChangeDetectionStrategy, Component, input, output} from '@angular/core'
import {RouterModule} from '@angular/router'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {TranslatePipe} from '@ngx-translate/core'
import {GameSessionPlayer, GovernmentType, Nation} from '@board-buddy/core'

export interface EnrichedParticipant {
  participant: GameSessionPlayer
  nation: Nation | null
  government: {type: GovernmentType} | null
}

@Component({
  selector: 'portal-session-content',
  imports: [RouterModule, MatButtonModule, MatIconModule, MatCardModule, TranslatePipe],
  templateUrl: './session-content.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionContentComponent {
  readonly participants = input.required<EnrichedParticipant[]>()
  readonly playerId = input.required<number | null>()
  readonly hostId = input.required<number | null>()
  readonly sessionId = input.required<string>()

  readonly attackPlayer = output<GameSessionPlayer>()
}
