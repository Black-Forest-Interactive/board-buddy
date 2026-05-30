import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./legal-notice.component').then(m => m.LegalNoticeComponent)}
]
