import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: 'new', loadComponent: () => import('../session-create/session-create.component').then(m => m.SessionCreateComponent)},
  {path: 'join', loadComponent: () => import('../session-join/session-join.component').then(m => m.SessionJoinComponent)},
  {path: ':key/army', loadComponent: () => import('./session-army/session-army.component').then(m => m.SessionArmyComponent)},
  {path: ':key/research', loadComponent: () => import('./session-research/session-research.component').then(m => m.SessionResearchComponent)},
  {path: ':key', loadComponent: () => import('./session.component').then(m => m.SessionComponent)},
]
