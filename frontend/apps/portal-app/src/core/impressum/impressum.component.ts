import {Component, inject} from '@angular/core'
import {Location} from '@angular/common'
import {MainContentComponent} from '@board-buddy/ui'
import {LEGAL} from '../legal/legal.config'

@Component({
  selector: 'portal-impressum',
  imports: [MainContentComponent],
  templateUrl: './impressum.component.html',
})
export class ImpressumComponent {
  private location = inject(Location)
  readonly legal = LEGAL
  back() { this.location.back() }
}
