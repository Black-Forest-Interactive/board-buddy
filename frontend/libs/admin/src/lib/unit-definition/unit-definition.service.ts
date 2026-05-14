import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {UnitDefinition, UnitDefinitionChangeRequest} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class UnitDefinitionService extends BaseService {
  constructor() { super('portal/unit-type') }

  getUnitDefinitions(page = 0, size = 20): Observable<Page<UnitDefinition>> {
    return this.getPaged<UnitDefinition>('', page, size)
  }

  getUnitDefinition(id: number): Observable<UnitDefinition> {
    return this.get<UnitDefinition>(id.toString())
  }

  create(request: UnitDefinitionChangeRequest): Observable<UnitDefinition> {
    return this.post<UnitDefinition>('', request)
  }

  update(id: number, request: UnitDefinitionChangeRequest): Observable<UnitDefinition> {
    return this.put<UnitDefinition>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }
}
