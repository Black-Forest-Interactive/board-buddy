import {ChangeDetectionStrategy, Component, computed, inject, input} from '@angular/core'
import { MatSidenavModule } from '@angular/material/sidenav'
import { MatIconModule } from '@angular/material/icon'
import { MatButtonModule } from '@angular/material/button'
import { RouterModule } from '@angular/router'
import { MatCardModule } from '@angular/material/card'
import { ShellService } from '../shell.service'
import { ShellMenuComponent } from '../menu/shell-menu.component'
import { ShellMenuGroup, ShellMenuItem } from '../menu/shell-menu'
import { ShellBottomNavComponent } from '../bottom-nav/shell-bottom-nav.component'

@Component({
  selector: 'ui-shell-content',
  imports: [
    RouterModule,
    MatSidenavModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    ShellMenuComponent,
    ShellBottomNavComponent,
  ],
  templateUrl: './shell-content.component.html',
  styleUrl: './shell-content.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShellContentComponent {
  readonly menuGroups = input<ShellMenuGroup[]>([])
  readonly bottomNavItems = input<ShellMenuItem[]>([])
  protected readonly service = inject(ShellService)
  protected readonly useBottomNav = computed(() => this.service.isHandset() && this.bottomNavItems().length > 0)
}
