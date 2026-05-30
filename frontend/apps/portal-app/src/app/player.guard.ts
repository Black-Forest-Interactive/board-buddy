import {inject} from '@angular/core'
import {CanActivateFn, Router} from '@angular/router'
import {PlayerService} from '@board-buddy/portal'

export const playerGuard: CanActivateFn = () => {
  const playerId = inject(PlayerService).getPlayerId()
  if (playerId === null) return inject(Router).createUrlTree(['/register'])
  return true
}
