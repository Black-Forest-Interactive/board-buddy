import {ChangeDetectionStrategy, Component, inject} from '@angular/core'
import {Location} from '@angular/common'
import {MainContentComponent} from '@board-buddy/ui'
import {LEGAL} from '../legal/legal.config'

@Component({
  selector: 'portal-legal-notice',
  imports: [MainContentComponent],
  templateUrl: './legal-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalNoticeComponent {
  private location = inject(Location)
  readonly legal = LEGAL
  back() { this.location.back() }
}
