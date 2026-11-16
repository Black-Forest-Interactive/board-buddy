import {inject, Injectable, signal} from '@angular/core'
import {catchError, firstValueFrom, Observable, of, tap} from 'rxjs'
import {PlayerService} from './player.service'
import {CreatePlayerRequest, PortalPlayer} from './player.api'

// cookie of the old client side identity, only removed on startup
const LEGACY_PLAYER_COOKIE = 'player-id'

/**
 * Holds the current portal player. The identity itself is an HttpOnly cookie set by the backend and not readable here,
 * so the player is loaded once via `me` on startup and kept in a signal.
 */
@Injectable({providedIn: 'root'})
export class CurrentPlayerService {
  private readonly service = inject(PlayerService)

  private readonly current = signal<PortalPlayer | null>(null)
  readonly player = this.current.asReadonly()

  load(): Promise<PortalPlayer | null> {
    document.cookie = `${LEGACY_PLAYER_COOKIE}=; path=/; max-age=0`
    return firstValueFrom(this.service.getMe().pipe(catchError(() => of(null)))).then(player => {
      this.current.set(player)
      return player
    })
  }

  createPlayer(request: CreatePlayerRequest): Observable<PortalPlayer> {
    return this.service.createPlayer(request).pipe(tap(player => this.current.set(player)))
  }

  logout(): Observable<void> {
    return this.service.logout().pipe(tap(() => this.current.set(null)))
  }

  getPlayerId(): number | null {
    return this.current()?.id ?? null
  }
}
