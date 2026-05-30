import {Component, inject} from '@angular/core'
import {Location} from '@angular/common'
import {MainContentComponent} from '@board-buddy/ui'
import {LEGAL} from '../legal/legal.config'

@Component({
  selector: 'portal-datenschutz',
  imports: [MainContentComponent],
  templateUrl: './datenschutz.component.html',
})
export class DatenschutzComponent {
  private location = inject(Location)
  readonly legal = LEGAL
  back() { this.location.back() }
}
