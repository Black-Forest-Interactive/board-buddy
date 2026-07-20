import {ChangeDetectionStrategy, Component, input} from '@angular/core'
import {RouterLink} from '@angular/router'
import {TranslatePipe} from '@ngx-translate/core'
import packageJson from '../../../../../../package.json'

@Component({
  selector: 'ui-shell-footer',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './shell-footer.component.html',
  styleUrl: './shell-footer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShellFooterComponent {
  readonly showLegalLinks = input(true)
  version = packageJson.version
  currentYear = new Date().getFullYear()
}
