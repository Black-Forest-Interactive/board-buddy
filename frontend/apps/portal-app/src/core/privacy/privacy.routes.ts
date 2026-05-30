import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./privacy.component').then(m => m.PrivacyComponent)}
]
