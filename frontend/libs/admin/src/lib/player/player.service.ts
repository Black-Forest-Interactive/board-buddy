import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {Player, PlayerChangeRequest} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class PlayerService extends BaseService {
  constructor() { super('admin/player') }

  getPlayers(page = 0, size = 20): Observable<Page<Player>> {
    return this.getPaged<Player>('', page, size)
  }

  getPlayer(id: number): Observable<Player> {
    return this.get<Player>(id.toString())
  }

  create(request: PlayerChangeRequest): Observable<Player> {
    return this.post<Player>('', request)
  }

  update(id: number, request: PlayerChangeRequest): Observable<Player> {
    return this.put<Player>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }
}
