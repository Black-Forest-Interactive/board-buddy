import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./datenschutz.component').then(m => m.DatenschutzComponent)}
]
