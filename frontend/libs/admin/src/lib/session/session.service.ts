import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {GameSession, GameSessionChangeRequest} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class SessionService extends BaseService {
  constructor() { super('admin/session') }

  getSessions(page = 0, size = 20): Observable<Page<GameSession>> {
    return this.getPaged<GameSession>('', page, size)
  }

  getSession(id: number): Observable<GameSession> {
    return this.get<GameSession>(id.toString())
  }

  create(request: GameSessionChangeRequest): Observable<GameSession> {
    return this.post<GameSession>('', request)
  }

  update(id: number, request: GameSessionChangeRequest): Observable<GameSession> {
    return this.put<GameSession>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }

  assignPlayer(sessionId: number, playerId: number): Observable<GameSession> {
    return this.post<GameSession>(`${sessionId}/player/${playerId}`, {})
  }

  revokePlayer(sessionId: number, playerId: number): Observable<GameSession> {
    return this.delete<GameSession>(`${sessionId}/player/${playerId}`)
  }
}
