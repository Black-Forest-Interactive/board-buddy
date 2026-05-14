import { Component, input, output } from '@angular/core'
import { MatCardModule } from '@angular/material/card'
import { SearchComponent } from '../search/search.component'

@Component({
  selector: 'ui-main-content',
  imports: [MatCardModule, SearchComponent],
  templateUrl: './main-content.component.html',
  styleUrl: './main-content.component.scss',
})
export class MainContentComponent {
  readonly title = input('')
  readonly cardClass = input('')
  readonly containerClass = input('')
  readonly enableSearch = input(false)
  readonly searchLabel = input('Search')

  readonly search = output<string>()

  handleSearch(query: string) {
    this.search.emit(query)
  }
}
