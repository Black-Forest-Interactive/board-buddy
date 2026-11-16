import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService} from '@board-buddy/shared'
import {CreatePlayerRequest, PortalPlayer} from './player.api'

@Injectable({providedIn: 'root'})
export class PlayerService extends BaseService {
  constructor() { super('portal/player') }

  getMe(): Observable<PortalPlayer> {
    return this.get<PortalPlayer>('me')
  }

  createPlayer(request: CreatePlayerRequest): Observable<PortalPlayer> {
    return this.post<PortalPlayer>('', request)
  }

  logout(): Observable<void> {
    return this.post<void>('logout', {})
  }
}
