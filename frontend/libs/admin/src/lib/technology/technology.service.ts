import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {Technology, TechnologyChangeRequest, TechnologyEffectUnitUnlockRequest, UnitType} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class TechnologyService extends BaseService {
  constructor() { super('portal/technology') }

  getTechnologies(page = 0, size = 20): Observable<Page<Technology>> {
    return this.getPaged<Technology>('', page, size)
  }

  getTechnology(id: number): Observable<Technology> {
    return this.get<Technology>(id.toString())
  }

  create(request: TechnologyChangeRequest): Observable<Technology> {
    return this.post<Technology>('', request)
  }

  update(id: number, request: TechnologyChangeRequest): Observable<Technology> {
    return this.put<Technology>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }

  assignUnitUnlock(id: number, request: TechnologyEffectUnitUnlockRequest): Observable<Technology> {
    return this.post<Technology>(`${id}/effect/unit-unlock`, request)
  }

  revokeUnitUnlock(id: number, unitType: UnitType, unitLevel: number): Observable<Technology> {
    return this.delete<Technology>(`${id}/effect/unit-unlock/${unitType}/${unitLevel}`)
  }
}
