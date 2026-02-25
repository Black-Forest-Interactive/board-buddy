import {Component} from '@angular/core';
import packageJson from '../../../../../package.json';

@Component({
  selector: 'app-portal-footer',
  imports: [],
  templateUrl: './portal-footer.component.html',
  styleUrl: './portal-footer.component.scss',
})
export class PortalFooterComponent {
  version = packageJson.version
  currentYear = new Date().getFullYear()
}
