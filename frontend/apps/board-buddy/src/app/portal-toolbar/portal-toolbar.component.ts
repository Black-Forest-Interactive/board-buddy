import {Component} from '@angular/core';
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatToolbar} from "@angular/material/toolbar";
import {RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {AppService} from "../app.service";


@Component({
  selector: 'app-portal-toolbar',
  imports: [
    MatButton,
    MatIcon,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    MatToolbar,
    RouterLink,
    TranslatePipe,
    MatMenuTrigger,
  ],
  templateUrl: './portal-toolbar.component.html',
  styleUrl: './portal-toolbar.component.scss',
})
export class PortalToolbarComponent {
  constructor(
    protected readonly service: AppService,
  ) {
  }

}
