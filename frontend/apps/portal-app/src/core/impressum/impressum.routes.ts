import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./impressum.component').then(m => m.ImpressumComponent)}
]
