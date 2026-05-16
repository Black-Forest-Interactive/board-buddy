import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {Game, GameChangeRequest} from '@board-buddy/core'

@Injectable({ providedIn: 'root' })
export class GameService extends BaseService {
  constructor() { super('admin/game') }

  getGames(page = 0, size = 20): Observable<Page<Game>> {
    return this.getPaged<Game>('', page, size)
  }

  getGame(id: number): Observable<Game> {
    return this.get<Game>(id.toString())
  }

  create(request: GameChangeRequest): Observable<Game> {
    return this.post<Game>('', request)
  }

  update(id: number, request: GameChangeRequest): Observable<Game> {
    return this.put<Game>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }

  assignRuleSet(gameId: number, ruleSetId: number): Observable<Game> {
    return this.post<Game>(`${gameId}/rule-set/${ruleSetId}`, {})
  }

  revokeRuleSet(gameId: number, ruleSetId: number): Observable<Game> {
    return this.delete<Game>(`${gameId}/rule-set/${ruleSetId}`)
  }
}
