import {Route} from '@angular/router'

export const appRoutes: Route[] = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', loadChildren: () => import('../core/home/home.routes').then(m => m.routes)},
  {path: 'rule-set', loadChildren: () => import('../core/rule-set/rule-set.routes').then(m => m.routes)},
  {path: 'game', loadChildren: () => import('../core/game/game.routes').then(m => m.routes)},
  {path: 'player', loadChildren: () => import('../core/player/player.routes').then(m => m.routes)},
  {path: 'unit-definition', loadChildren: () => import('../core/unit-definition/unit-definition.routes').then(m => m.routes)},
  {path: 'technology', loadChildren: () => import('../core/technology/technology.routes').then(m => m.routes)},
  {path: 'session', loadChildren: () => import('../core/session/session.routes').then(m => m.routes)},
]
