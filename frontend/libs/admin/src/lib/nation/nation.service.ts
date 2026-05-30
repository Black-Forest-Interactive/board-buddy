import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {GovernmentType, Nation, NationChangeRequest, NationEffectInitialGovernmentRequest} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class NationService extends BaseService {
  constructor() { super('portal/nation') }

  getNations(page = 0, size = 100): Observable<Page<Nation>> {
    return this.getPaged<Nation>('', page, size)
  }

  getNation(id: number): Observable<Nation> {
    return this.get<Nation>(id.toString())
  }

  create(request: NationChangeRequest): Observable<Nation> {
    return this.post<Nation>('', request)
  }

  update(id: number, request: NationChangeRequest): Observable<Nation> {
    return this.put<Nation>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }

  assignInitialGovernment(id: number, request: NationEffectInitialGovernmentRequest): Observable<Nation> {
    return this.post<Nation>(`${id}/effect/initial-government`, request)
  }

  revokeInitialGovernment(id: number, type: GovernmentType): Observable<Nation> {
    return this.delete<Nation>(`${id}/effect/initial-government/${type}`)
  }
}
