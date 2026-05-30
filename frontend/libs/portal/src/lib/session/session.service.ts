import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService} from '@board-buddy/shared'
import {Game, GameSession, Workflow} from '@board-buddy/core'
import {PortalCreateSessionRequest, PortalJoinSessionRequest} from './session.api'

@Injectable({providedIn: 'root'})
export class PortalSessionService extends BaseService {
  constructor() { super('portal/session') }

  getSessions(): Observable<Workflow[]> {
    return this.getAll<Workflow>('')
  }

  getSessionByKey(key: string): Observable<GameSession> {
    return this.get<GameSession>(`by-key/${key}`)
  }

  getGames(): Observable<Game[]> {
    return this.getAll<Game>('games')
  }

  createSession(request: PortalCreateSessionRequest): Observable<Workflow> {
    return this.post<Workflow>('', request)
  }

  joinSession(key: string, request: PortalJoinSessionRequest): Observable<Workflow> {
    return this.post<Workflow>(`join/${key}`, request)
  }
}
