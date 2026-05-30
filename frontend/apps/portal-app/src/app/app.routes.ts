import {Route} from '@angular/router'
import {playerGuard} from './player.guard'

export const appRoutes: Route[] = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'register', loadChildren: () => import('../core/register/register.routes').then(m => m.routes)},
  {path: 'home', canActivate: [playerGuard], loadChildren: () => import('../core/home/home.routes').then(m => m.routes)},
  {path: 'player', canActivate: [playerGuard], loadChildren: () => import('../core/player/player.routes').then(m => m.routes)},
  {path: 'session', canActivate: [playerGuard], loadChildren: () => import('../core/session/session.routes').then(m => m.routes)},
]
