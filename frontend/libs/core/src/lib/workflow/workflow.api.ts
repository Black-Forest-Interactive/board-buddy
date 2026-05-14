import {Player} from '../player/player.api'
import {Game} from '../game/game.api'
import {RuleSet} from '../rule-set/rule-set.api'
import {UnitType} from '../unit/unit.api'

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
  player: Player
  units: GameUnit[]
  fronts: BattleFrontInfo[]
}

export interface BattleInfo {
  participant: BattleParticipantInfo[]
  activePlayer: Player
}

export interface BattleFront {
  index: number
  unit: number
}

export interface BattleParticipant {
  player: Player
  units: number[]
  fronts: BattleFront[]
}

export interface Battle {
  participant: BattleParticipant[]
  activePlayer: Player
}

export interface Workflow {
  id: string
  name: string
  host: Player
  participants: Player[]
  game: Game
  ruleSet: RuleSet
  timestamp: string
  activeBattle: Battle | null
}

export interface WorkflowParticipantInfo {
  player: Player
  units: GameUnit[]
}

export class WorkflowBattleStartRequest {
  constructor(
    public attackerId: number,
    public defenderId: number,
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
