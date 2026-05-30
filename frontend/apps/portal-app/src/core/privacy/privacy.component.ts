import {Component, inject} from '@angular/core'
import {Location} from '@angular/common'
import {MainContentComponent} from '@board-buddy/ui'
import {LEGAL} from '../legal/legal.config'

@Component({
  selector: 'portal-privacy',
  imports: [MainContentComponent],
  templateUrl: './privacy.component.html',
})
export class PrivacyComponent {
  private location = inject(Location)
  readonly legal = LEGAL
  back() { this.location.back() }
}
