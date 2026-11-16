import {inject} from '@angular/core'
import {CanActivateFn, Router} from '@angular/router'
import {CurrentPlayerService} from '@board-buddy/portal'

export const playerGuard: CanActivateFn = () => {
  const playerId = inject(CurrentPlayerService).getPlayerId()
  if (playerId === null) return inject(Router).createUrlTree(['/register'])
  return true
}
