import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./technology.component').then(m => m.TechnologyComponent)},
  {path: ':id', loadComponent: () => import('./technology-detail/technology-detail.component').then(m => m.TechnologyDetailComponent)},
]
