import {Player} from '../player/player.api'
import {Game} from '../game/game.api'
import {RuleSet} from '../rule-set/rule-set.api'
import {UnitType} from '../unit/unit.api'
import {Technology} from '../technology/technology.api'
import {GovernmentType} from '../nation/nation.api'

export const BattleType = {
  ARMY_VS_ARMY: 'ARMY_VS_ARMY',
  ARMY_VS_CITY: 'ARMY_VS_CITY',
  ARMY_VS_BARBARIANS: 'ARMY_VS_BARBARIANS',
} as const
export type BattleType = typeof BattleType[keyof typeof BattleType]

export interface GameSessionPlayer {
  player: Player
  entity: number
}

export interface GameUnit {
  entity: number
  damage: {amount: number} | null
  health: {amount: number} | null
  level: {value: number} | null
  type: {kind: UnitType} | null
  counterType: {kind: UnitType} | null
}

export const BattleStatus = {
  INIT: 'INIT',
  ONGOING: 'ONGOING',
  FINISHED: 'FINISHED',
} as const
export type BattleStatus = typeof BattleStatus[keyof typeof BattleStatus]

export const BattleActivity = {
  CREATE_FRONT: 'CREATE_FRONT',
  ATTACK_FRONT: 'ATTACK_FRONT',
} as const
export type BattleActivity = typeof BattleActivity[keyof typeof BattleActivity]

export const CombatActionType = {
  DAMAGE_DEALT: 'DAMAGE_DEALT',
  DAMAGE_TAKEN: 'DAMAGE_TAKEN',
  UNIT_DESTROYED: 'UNIT_DESTROYED',
} as const
export type CombatActionType = typeof CombatActionType[keyof typeof CombatActionType]

export interface CombatAction {
  type: CombatActionType
  unit: number
  amount?: number
}

export interface BattleFrontUnit {
  player: GameSessionPlayer
  unit: GameUnit
  currentHealth: number
}

export interface BattleFront {
  index: number
  units: BattleFrontUnit[]
}

export interface BattleLogEntry {
  player: GameSessionPlayer
  activity: BattleActivity
  actions: CombatAction[]
}

export interface BattleParticipant {
  player: GameSessionPlayer
  armyCount: number
  units: GameUnit[]
}

export interface Battle {
  participant: BattleParticipant[]
  fronts: BattleFront[]
  logEntries: BattleLogEntry[]
  activePlayer: GameSessionPlayer
  status: BattleStatus
  winner: GameSessionPlayer | null
}

export interface Workflow {
  id: string
  name: string
  host: Player
  participants: GameSessionPlayer[]
  game: Game
  ruleSet: RuleSet
  timestamp: string
  activeBattle: Battle | null
}

export interface WorkflowParticipantInfo {
  player: Player
  nation: {id: number} | null
  government: {type: GovernmentType} | null
  units: GameUnit[]
  unitLevel: Record<string, number>
  technologies: Technology[]
  availableTechnologies: Technology[]
}

export interface TechnologyStatus {
  researched: Technology[]
  available: Technology[]
  blocked: Technology[]
}

export class WorkflowAssignPlayerRequest {
  constructor(
    public playerId: number,
    public nationId: number,
  ) {}
}

export class WorkflowCreateRequest {
  constructor(
    public name: string,
    public hostId: number,
    public gameId: number,
    public ruleSetId: number,
    public nationId: number,
  ) {}
}

export class WorkflowPlayerJoinRequest {
  constructor(
    public name: string,
    public nationId: number,
  ) {}
}

export class BattleParticipantRequest {
  constructor(
    public id: number,
    public armyCount: number,
  ) {}
}

export class WorkflowBattleStartRequest {
  constructor(
    public attacker: BattleParticipantRequest,
    public defender: BattleParticipantRequest,
    public type: BattleType,
    public isWalled: boolean,
  ) {}
}

export class WorkflowCreateUnitRequest {
  constructor(
    public playerId: number,
    public unitTypeId: number,
  ) {}
}

export class WorkflowBattleCreateFrontRequest {
  constructor(
    public playerId: number,
    public entityId: number,
  ) {}
}

export class WorkflowBattleAttackFrontRequest {
  constructor(
    public attackerId: number,
    public defenderId: number,
    public entityId: number,
    public frontIndex: number,
  ) {}
}

export class WorkflowResearchRequest {
  constructor(
    public playerId: number,
    public technologyId: number,
  ) {}
}
