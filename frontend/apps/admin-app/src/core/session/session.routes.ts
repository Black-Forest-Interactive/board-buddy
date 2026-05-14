import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./session.component').then(m => m.SessionComponent)},
  {path: ':id', loadComponent: () => import('./session-detail/session-detail.component').then(m => m.SessionDetailComponent)},
]
