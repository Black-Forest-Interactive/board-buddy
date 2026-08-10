import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: 'new', loadComponent: () => import('../session-create/session-create.component').then(m => m.SessionCreateComponent)},
  {path: 'join', loadComponent: () => import('../session-join/session-join.component').then(m => m.SessionJoinComponent)},
  {
    path: ':key',
    loadComponent: () => import('./session.component').then(m => m.SessionComponent),
    children: [
      {path: '', pathMatch: 'full', loadComponent: () => import('./session-lobby/session-lobby.component').then(m => m.SessionLobbyComponent)},
      {path: 'army', loadComponent: () => import('./session-army/session-army.component').then(m => m.SessionArmyComponent)},
      {path: 'research', loadComponent: () => import('./session-research/session-research.component').then(m => m.SessionResearchComponent)},
      {path: 'battle', loadComponent: () => import('./session-battle/session-battle.component').then(m => m.SessionBattleComponent)},
    ]
  },
]
