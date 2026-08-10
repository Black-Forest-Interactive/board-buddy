import {ChangeDetectionStrategy, Component, input, output, ViewEncapsulation} from '@angular/core'
import {MatCardModule} from '@angular/material/card'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {SearchComponent} from '../search/search.component'
import {MatToolbar} from '@angular/material/toolbar'

@Component({
  selector: 'ui-main-content',
  imports: [MatCardModule, MatButtonModule, MatIconModule, SearchComponent, MatToolbar],
  templateUrl: './main-content.component.html',
  styleUrl: './main-content.component.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainContentComponent {
  readonly title = input('')
  readonly containerClass = input('')
  readonly enableSearch = input(false)
  readonly searchLabel = input('Search')
  readonly showBack = input(false)

  readonly search = output<string>()
  readonly back = output()

  handleSearch(query: string) { this.search.emit(query) }
}
