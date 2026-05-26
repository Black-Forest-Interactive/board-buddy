import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService} from '@board-buddy/shared'
import {Nation, TechnologyStatus, Workflow, WorkflowBattleAttackFrontRequest, WorkflowBattleCreateFrontRequest, WorkflowBattleStartRequest, WorkflowCreateUnitRequest, WorkflowParticipantInfo, WorkflowResearchRequest} from '@board-buddy/core'
import {PortalBattle, PortalParticipantInfo} from './workflow.api'

@Injectable({providedIn: 'root'})
export class PortalWorkflowService extends BaseService {
  constructor() { super('portal/workflow') }

  getWorkflow(key: string): Observable<Workflow> {
    return this.get<Workflow>(key)
  }

  getAvailableNations(key: string): Observable<Nation[]> {
    return this.getAll<Nation>(`${key}/nations`)
  }

  getTechnologyStatus(key: string): Observable<TechnologyStatus> {
    return this.get<TechnologyStatus>(`${key}/technology-status`)
  }

  getParticipantsInfo(key: string): Observable<WorkflowParticipantInfo[]> {
    return this.getAll<WorkflowParticipantInfo>(`${key}/participants`)
  }

  getMyInfo(key: string): Observable<PortalParticipantInfo> {
    return this.get<PortalParticipantInfo>(`${key}/my-info`)
  }

  getBattle(key: string): Observable<PortalBattle> {
    return this.get<PortalBattle>(`${key}/battle`)
  }

  createUnit(key: string, request: WorkflowCreateUnitRequest): Observable<Workflow> {
    return this.post<Workflow>(`${key}/unit`, request)
  }

  research(key: string, request: WorkflowResearchRequest): Observable<Workflow> {
    return this.post<Workflow>(`${key}/research`, request)
  }

  battleStart(key: string, request: WorkflowBattleStartRequest): Observable<Workflow> {
    return this.post<Workflow>(`${key}/battle/start`, request)
  }

  battleCreateFront(key: string, request: WorkflowBattleCreateFrontRequest): Observable<PortalBattle> {
    return this.post<PortalBattle>(`${key}/battle/front`, request)
  }

  battleAttackFront(key: string, request: WorkflowBattleAttackFrontRequest): Observable<PortalBattle> {
    return this.post<PortalBattle>(`${key}/battle/attack`, request)
  }

  battleFinish(key: string): Observable<void> {
    return this.post<void>(`${key}/battle/finish`, {})
  }
}
