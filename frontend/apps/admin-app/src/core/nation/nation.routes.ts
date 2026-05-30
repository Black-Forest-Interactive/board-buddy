import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./nation.component').then(m => m.NationComponent)},
  {path: ':id', loadComponent: () => import('./nation-detail/nation-detail.component').then(m => m.NationDetailComponent)},
]
