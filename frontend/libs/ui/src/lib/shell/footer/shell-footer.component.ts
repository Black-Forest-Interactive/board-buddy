import {ChangeDetectionStrategy, Component, inject, input} from '@angular/core'
import {RouterLink} from '@angular/router'
import {TranslatePipe} from '@ngx-translate/core'
import {ShellService} from '../shell.service'
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
  protected readonly service = inject(ShellService)
  version = packageJson.version
  currentYear = new Date().getFullYear()
}
