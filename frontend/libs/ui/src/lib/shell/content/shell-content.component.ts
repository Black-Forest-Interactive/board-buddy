import { Component, inject, input } from '@angular/core'
import { MatSidenavModule } from '@angular/material/sidenav'
import { MatIconModule } from '@angular/material/icon'
import { MatButtonModule } from '@angular/material/button'
import { RouterModule } from '@angular/router'
import { MatCardModule } from '@angular/material/card'
import { ShellService } from '../shell.service'
import { ShellMenuComponent } from '../menu/shell-menu.component'
import { ShellMenuGroup } from '../menu/shell-menu'

@Component({
  selector: 'ui-shell-content',
  imports: [
    RouterModule,
    MatSidenavModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    ShellMenuComponent,
  ],
  templateUrl: './shell-content.component.html',
  styleUrl: './shell-content.component.scss',
})
export class ShellContentComponent {
  readonly menuGroups = input<ShellMenuGroup[]>([])
  protected readonly service = inject(ShellService)
}
