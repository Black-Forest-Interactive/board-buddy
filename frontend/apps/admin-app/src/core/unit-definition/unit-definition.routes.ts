import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./unit-definition.component').then(m => m.UnitDefinitionComponent)}
]
