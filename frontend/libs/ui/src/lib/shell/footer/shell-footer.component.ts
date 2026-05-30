import {Component} from '@angular/core'
import {RouterLink} from '@angular/router'
import packageJson from '../../../../../../package.json'

@Component({
  selector: 'ui-shell-footer',
  imports: [RouterLink],
  templateUrl: './shell-footer.component.html',
  styleUrl: './shell-footer.component.scss',
})
export class ShellFooterComponent {
  version = packageJson.version
  currentYear = new Date().getFullYear()
}
