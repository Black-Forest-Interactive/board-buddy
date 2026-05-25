import {Component} from '@angular/core'
import {ShellComponent, ShellMenuGroup} from '@board-buddy/ui'

@Component({
  imports: [ShellComponent],
  selector: 'admin-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  readonly menuGroups: ShellMenuGroup[] = [
    {
      title: 'MENU.group.overview',
      items: [
        {routerLink: '/home', icon: 'dashboard', text: 'home.title'},
      ]
    },
    {
      title: 'MENU.group.configuration',
      items: [
        {routerLink: '/rule-set', icon: 'rule', text: 'rule-set.title'},
        {routerLink: '/unit-definition', icon: 'military_tech', text: 'unit-definition.title'},
        {routerLink: '/technology', icon: 'science', text: 'technology.title'},
        {routerLink: '/nation', icon: 'public', text: 'nation.title'},
        {routerLink: '/game', icon: 'sports_esports', text: 'game.title'},
      ]
    },
    {
      title: 'MENU.group.gameplay',
      items: [
        {routerLink: '/player', icon: 'person', text: 'player.title'},
        {routerLink: '/session', icon: 'casino', text: 'session.title'},
      ]
    },
  ]
}
