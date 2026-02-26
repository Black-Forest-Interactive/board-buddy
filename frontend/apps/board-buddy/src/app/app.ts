import {Component} from '@angular/core';
import {RouterModule} from '@angular/router';
import {PortalToolbarComponent} from "./portal-toolbar/portal-toolbar.component";
import {PortalContentComponent} from "./portal-content/portal-content.component";
import {PortalFooterComponent} from "./portal-footer/portal-footer.component";

@Component({
  imports: [RouterModule, PortalToolbarComponent, PortalContentComponent, PortalFooterComponent],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected title = 'portal-app';
}
