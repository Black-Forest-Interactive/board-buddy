import {Player} from '../player/player.api'
import {Game} from '../game/game.api'
import {RuleSet} from '../rule-set/rule-set.api'
import {UnitType} from '../unit/unit.api'

export const NationType = {
  AMERICA: 'AMERICA',
  CHINA: 'CHINA',
  EGYPT: 'EGYPT',
  GERMANY: 'GERMANY',
  ROME: 'ROME',
  RUSSIA: 'RUSSIA',
  ARABS: 'ARABS',
  GREEKS: 'GREEKS',
  INDIANS: 'INDIANS',
  SPANISH: 'SPANISH',
  AZTECS: 'AZTECS',
  ENGLISH: 'ENGLISH',
  FRENCH: 'FRENCH',
  JAPANESE: 'JAPANESE',
  MONGOLS: 'MONGOLS',
  ZULU: 'ZULU',
} as const
export type NationType = typeof NationType[keyof typeof NationType]

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

export interface BattleFrontInfo {
  index: number
  unit: GameUnit
}

export interface BattleParticipantInfo {
  player: GameSessionPlayer
  armyCount: number
  units: GameUnit[]
  fronts: BattleFrontInfo[]
}

export interface BattleInfo {
  participant: BattleParticipantInfo[]
  activePlayer: GameSessionPlayer
}

export interface BattleFront {
  index: number
  unit: number
}

export interface BattleParticipant {
  player: GameSessionPlayer
  armyCount: number
  units: number[]
  fronts: BattleFront[]
}

export interface Battle {
  participant: BattleParticipant[]
  activePlayer: GameSessionPlayer
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
  player: GameSessionPlayer
  units: GameUnit[]
}

export class WorkflowAssignPlayerRequest {
  constructor(
    public playerId: number,
    public nation: NationType,
  ) {}
}

export class WorkflowCreateRequest {
  constructor(
    public name: string,
    public hostId: number,
    public gameId: number,
    public ruleSetId: number,
    public nation: NationType,
  ) {}
}

export class WorkflowPlayerJoinRequest {
  constructor(
    public name: string,
    public nation: NationType,
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
