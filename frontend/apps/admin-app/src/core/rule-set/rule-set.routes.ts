import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./rule-set.component').then(m => m.RuleSetComponent)},
  {path: ':id', loadComponent: () => import('./rule-set-detail/rule-set-detail.component').then(m => m.RuleSetDetailComponent)},
]
