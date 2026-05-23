import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService} from '@board-buddy/shared'
import {CreatePlayerRequest, PortalPlayer} from './player.api'

export const PLAYER_COOKIE = 'player-id'

@Injectable({providedIn: 'root'})
export class PlayerService extends BaseService {
  constructor() { super('portal/player') }

  getPlayer(id: number): Observable<PortalPlayer> {
    return this.get<PortalPlayer>(id.toString())
  }

  createPlayer(request: CreatePlayerRequest): Observable<PortalPlayer> {
    return this.post<PortalPlayer>('', request)
  }

  getPlayerId(): number | null {
    const match = document.cookie.split(';').map(c => c.trim()).find(c => c.startsWith(PLAYER_COOKIE + '='))
    if (!match) return null
    const val = parseInt(match.split('=')[1], 10)
    return isNaN(val) ? null : val
  }

  setPlayerId(id: number): void {
    document.cookie = `${PLAYER_COOKIE}=${id}; path=/; max-age=${365 * 24 * 3600}; SameSite=Lax`
  }

  clearPlayerId(): void {
    document.cookie = `${PLAYER_COOKIE}=; path=/; max-age=0`
  }
}
