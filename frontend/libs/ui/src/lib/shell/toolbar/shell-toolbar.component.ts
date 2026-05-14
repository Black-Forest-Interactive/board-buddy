import { Component, inject } from '@angular/core'
import { MatButton, MatIconButton } from '@angular/material/button'
import { MatIcon } from '@angular/material/icon'
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu'
import { MatToolbar } from '@angular/material/toolbar'
import { TranslatePipe } from '@ngx-translate/core'
import { RouterLink } from '@angular/router'
import { ThemeToggleButtonComponent } from '../../theme-toggle-button/theme-toggle-button.component'
import { ShellService } from '../shell.service'

@Component({
  selector: 'ui-shell-toolbar',
  imports: [
    MatButton,
    MatIcon,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    MatToolbar,
    TranslatePipe,
    MatMenuTrigger,
    ThemeToggleButtonComponent,
    RouterLink,
  ],
  templateUrl: './shell-toolbar.component.html',
  styleUrl: './shell-toolbar.component.scss',
})
export class ShellToolbarComponent {
  protected readonly service = inject(ShellService)

  showHelp() {
    window.open('https://github.com/Black-Forest-Interactive/board-buddy', '_blank')
  }
}
