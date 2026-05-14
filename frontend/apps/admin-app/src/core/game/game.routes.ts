import {Routes} from '@angular/router'

export const routes: Routes = [
  {path: '', loadComponent: () => import('./game.component').then(m => m.GameComponent)},
  {path: ':id', loadComponent: () => import('./game-detail/game-detail.component').then(m => m.GameDetailComponent)},
]
