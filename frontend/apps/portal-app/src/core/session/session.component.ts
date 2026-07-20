import {ChangeDetectionStrategy, Component} from '@angular/core'
import {RouterOutlet} from "@angular/router"

@Component({
  selector: 'portal-session',
  imports: [
    RouterOutlet
  ],
  templateUrl: './session.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionComponent {}
