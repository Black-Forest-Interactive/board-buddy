import {Component, input} from '@angular/core'
import {MatCardModule} from '@angular/material/card'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe} from '@ngx-translate/core'

@Component({
  selector: 'portal-session-qrcode',
  imports: [MatCardModule, MatButtonModule, MatIconModule, MatTooltipModule, TranslatePipe],
  templateUrl: './session-qrcode.component.html',
})
export class SessionQrcodeComponent {
  readonly sessionId = input.required<string>()
  readonly qrUrl = input.required<string>()

  copyId() { navigator.clipboard.writeText(this.sessionId()) }
}
