import {ChangeDetectionStrategy, Component, input} from '@angular/core'
import {RouterModule} from '@angular/router'
import {MatIconModule} from '@angular/material/icon'
import {TranslatePipe} from '@ngx-translate/core'
import {ShellMenuItem} from '../menu/shell-menu'

@Component({
  selector: 'ui-shell-bottom-nav',
  imports: [RouterModule, MatIconModule, TranslatePipe],
  templateUrl: './shell-bottom-nav.component.html',
  styleUrl: './shell-bottom-nav.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShellBottomNavComponent {
  readonly items = input.required<ShellMenuItem[]>()
}
