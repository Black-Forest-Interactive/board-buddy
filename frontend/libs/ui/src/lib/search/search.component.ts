import { Component, input, output } from '@angular/core'
import { MatFormFieldModule } from '@angular/material/form-field'
import { TranslatePipe } from '@ngx-translate/core'
import { MatIconModule } from '@angular/material/icon'
import { MatInputModule } from '@angular/material/input'
import { MatButtonModule } from '@angular/material/button'
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs'
import { takeUntilDestroyed } from '@angular/core/rxjs-interop'

@Component({
  selector: 'ui-search',
  imports: [MatFormFieldModule, MatIconModule, MatInputModule, MatButtonModule, TranslatePipe],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss',
  styles: [':host { display: block }'],
})
export class SearchComponent {
  readonly label = input('')
  readonly icon = input('search')
  readonly disabled = input(false)

  readonly search = output<string>()

  private keyUp = new Subject<string>()

  constructor() {
    this.keyUp.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntilDestroyed()
    ).subscribe(value => this.search.emit(value))
  }

  handleKeyUp(value: string) {
    this.keyUp.next(value)
  }

  clearSearch(input: HTMLInputElement) {
    input.value = ''
    this.search.emit('')
  }
}
