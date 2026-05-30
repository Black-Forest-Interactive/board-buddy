import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService, Page} from '@board-buddy/shared'
import {RuleSet, RuleSetChangeRequest} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class RuleSetService extends BaseService {
  constructor() { super('portal/rule-set') }

  getRuleSets(page = 0, size = 20): Observable<Page<RuleSet>> {
    return this.getPaged<RuleSet>('', page, size)
  }

  getRuleSet(id: number): Observable<RuleSet> {
    return this.get<RuleSet>(id.toString())
  }

  create(request: RuleSetChangeRequest): Observable<RuleSet> {
    return this.post<RuleSet>('', request)
  }

  update(id: number, request: RuleSetChangeRequest): Observable<RuleSet> {
    return this.put<RuleSet>(id.toString(), request)
  }

  remove(id: number): Observable<void> {
    return this.delete<void>(id.toString())
  }

  assignUnitDefinition(ruleSetId: number, unitDefinitionId: number): Observable<RuleSet> {
    return this.post<RuleSet>(`${ruleSetId}/unit-definition/${unitDefinitionId}`, {})
  }

  revokeUnitDefinition(ruleSetId: number, unitDefinitionId: number): Observable<RuleSet> {
    return this.delete<RuleSet>(`${ruleSetId}/unit-definition/${unitDefinitionId}`)
  }

  assignTechnology(ruleSetId: number, technologyId: number): Observable<RuleSet> {
    return this.post<RuleSet>(`${ruleSetId}/technology/${technologyId}`, {})
  }

  revokeTechnology(ruleSetId: number, technologyId: number): Observable<RuleSet> {
    return this.delete<RuleSet>(`${ruleSetId}/technology/${technologyId}`)
  }

  assignNation(ruleSetId: number, nationId: number): Observable<RuleSet> {
    return this.post<RuleSet>(`${ruleSetId}/nation/${nationId}`, {})
  }

  revokeNation(ruleSetId: number, nationId: number): Observable<RuleSet> {
    return this.delete<RuleSet>(`${ruleSetId}/nation/${nationId}`)
  }
}
