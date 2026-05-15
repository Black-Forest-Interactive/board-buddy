import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService} from '@board-buddy/shared'
import {
  BattleInfo,
  Workflow,
  WorkflowAssignPlayerRequest,
  WorkflowCreateRequest,
  WorkflowParticipantInfo,
  WorkflowBattleAttackFrontRequest,
  WorkflowBattleCreateFrontRequest,
  WorkflowBattleStartRequest,
  WorkflowCreateUnitRequest,
} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class WorkflowService extends BaseService {
  constructor() { super('admin/workflow') }

  create(request: WorkflowCreateRequest): Observable<Workflow> {
    return this.post<Workflow>('', request)
  }

  assignPlayer(sessionKey: string, request: WorkflowAssignPlayerRequest): Observable<Workflow> {
    return this.post<Workflow>(`${sessionKey}/player`, request)
  }

  getWorkflow(sessionKey: string): Observable<Workflow> {
    return this.get<Workflow>(sessionKey)
  }

  getParticipantsInfo(sessionKey: string): Observable<WorkflowParticipantInfo[]> {
    return this.get<WorkflowParticipantInfo[]>(`${sessionKey}/participants`)
  }

  createUnit(sessionKey: string, request: WorkflowCreateUnitRequest): Observable<Workflow> {
    return this.post<Workflow>(`${sessionKey}/unit`, request)
  }

  battleStart(sessionKey: string, request: WorkflowBattleStartRequest): Observable<Workflow> {
    return this.post<Workflow>(`${sessionKey}/battle/start`, request)
  }

  getBattleInfo(sessionKey: string): Observable<BattleInfo> {
    return this.get<BattleInfo>(`${sessionKey}/battle`)
  }

  battleCreateFront(sessionKey: string, request: WorkflowBattleCreateFrontRequest): Observable<BattleInfo> {
    return this.post<BattleInfo>(`${sessionKey}/battle/front`, request)
  }

  battleAttackFront(sessionKey: string, request: WorkflowBattleAttackFrontRequest): Observable<BattleInfo> {
    return this.post<BattleInfo>(`${sessionKey}/battle/attack`, request)
  }
}
