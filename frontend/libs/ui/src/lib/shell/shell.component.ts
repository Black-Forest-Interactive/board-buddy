import { Component } from '@angular/core'
import { ShellToolbarComponent } from './toolbar/shell-toolbar.component'
import { ShellContentComponent } from './content/shell-content.component'
import { ShellFooterComponent } from './footer/shell-footer.component'

@Component({
  selector: 'ui-shell',
  imports: [ShellToolbarComponent, ShellContentComponent, ShellFooterComponent],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {}
