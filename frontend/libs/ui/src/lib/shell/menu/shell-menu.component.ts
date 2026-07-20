import {ChangeDetectionStrategy, Component, input, signal} from '@angular/core'
import { MatIconModule } from '@angular/material/icon'
import { MatDividerModule } from '@angular/material/divider'
import { RouterLink, RouterLinkActive } from '@angular/router'
import { TranslatePipe } from '@ngx-translate/core'
import { MatIconButton } from '@angular/material/button'
import { NgClass } from '@angular/common'
import { ShellMenuGroup } from './shell-menu'

@Component({
  selector: 'ui-shell-menu',
  imports: [
    MatIconModule,
    MatDividerModule,
    RouterLink,
    RouterLinkActive,
    TranslatePipe,
    MatIconButton,
    NgClass,
  ],
  templateUrl: './shell-menu.component.html',
  styleUrl: './shell-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShellMenuComponent {
  readonly menuGroups = input<ShellMenuGroup[]>([])
  readonly collapsed = signal(false)
}
