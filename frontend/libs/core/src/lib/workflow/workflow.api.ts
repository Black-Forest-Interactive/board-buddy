import {Player} from '../player/player.api'
import {Game} from '../game/game.api'
import {RuleSet} from '../rule-set/rule-set.api'
import {UnitType} from '../unit/unit.api'

export const TechnologyType = {
  ANIMAL_HUSBANDRY: 'ANIMAL_HUSBANDRY',
  POTTERY: 'POTTERY',
  MINING: 'MINING',
  SAILING: 'SAILING',
  PHILOSOPHY: 'PHILOSOPHY',
  MASONRY: 'MASONRY',
  METAL_WORKING: 'METAL_WORKING',
  CODE_OF_LAWS: 'CODE_OF_LAWS',
  MYSTICISM: 'MYSTICISM',
  HORSEBACK_RIDING: 'HORSEBACK_RIDING',
  AGRICULTURE: 'AGRICULTURE',
  CURRENCY: 'CURRENCY',
  CONSTRUCTION: 'CONSTRUCTION',
  MATHEMATICS: 'MATHEMATICS',
  IRRIGATION: 'IRRIGATION',
  IRON_WORKING: 'IRON_WORKING',
  THEOLOGY: 'THEOLOGY',
  DRAMA_AND_POETRY: 'DRAMA_AND_POETRY',
  LITERATURE: 'LITERATURE',
  CIVIL_SERVICE: 'CIVIL_SERVICE',
  EDUCATION: 'EDUCATION',
  ENGINEERING: 'ENGINEERING',
  MACHINERY: 'MACHINERY',
  CHIVALRY: 'CHIVALRY',
  MILITARY_TRADITION: 'MILITARY_TRADITION',
  ACOUSTICS: 'ACOUSTICS',
  GUNPOWDER: 'GUNPOWDER',
  NAVIGATION: 'NAVIGATION',
  PRINTING_PRESS: 'PRINTING_PRESS',
  FLIGHT: 'FLIGHT',
  DEMOCRACY: 'DEMOCRACY',
  COMMUNISM: 'COMMUNISM',
  FUNDAMENTALISM: 'FUNDAMENTALISM',
  SPACE_FLIGHT: 'SPACE_FLIGHT',
} as const
export type TechnologyType = typeof TechnologyType[keyof typeof TechnologyType]

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
  player: GameSessionPlayer
  units: GameUnit[]
  technologies: TechnologyType[]
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

export class WorkflowResearchRequest {
  constructor(
    public playerId: number,
    public technology: TechnologyType,
  ) {}
}

export const TECHNOLOGY_TIER: Record<TechnologyType, number> = {
  ANIMAL_HUSBANDRY: 1, POTTERY: 1, MINING: 1, SAILING: 1, PHILOSOPHY: 1,
  MASONRY: 1, METAL_WORKING: 1, CODE_OF_LAWS: 1, MYSTICISM: 1, HORSEBACK_RIDING: 1, AGRICULTURE: 1,
  CURRENCY: 2, CONSTRUCTION: 2, MATHEMATICS: 2, IRRIGATION: 2, IRON_WORKING: 2, THEOLOGY: 2, DRAMA_AND_POETRY: 2, LITERATURE: 2,
  CIVIL_SERVICE: 3, EDUCATION: 3, ENGINEERING: 3, MACHINERY: 3, CHIVALRY: 3, MILITARY_TRADITION: 3, ACOUSTICS: 3,
  GUNPOWDER: 4, NAVIGATION: 4, PRINTING_PRESS: 4, FLIGHT: 4, DEMOCRACY: 4, COMMUNISM: 4, FUNDAMENTALISM: 4,
  SPACE_FLIGHT: 5,
}
