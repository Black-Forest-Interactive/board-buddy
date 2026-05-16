import {Component, input} from '@angular/core'
import {ShellToolbarComponent} from './toolbar/shell-toolbar.component'
import {ShellContentComponent} from './content/shell-content.component'
import {ShellFooterComponent} from './footer/shell-footer.component'
import {ShellMenuGroup} from './menu/shell-menu'

@Component({
  selector: 'ui-shell',
  imports: [ShellToolbarComponent, ShellContentComponent, ShellFooterComponent],
  templateUrl: './shell.component.html',
})
export class ShellComponent {
  readonly menuGroups = input<ShellMenuGroup[]>([])
}
