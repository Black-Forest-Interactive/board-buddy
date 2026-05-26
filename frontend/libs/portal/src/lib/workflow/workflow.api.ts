import {BattleFront, BattleLogEntry, BattleParticipant, BattleStatus, GameSessionPlayer, GameUnit, GovernmentType, Player, Technology} from '@board-buddy/core'

export interface PortalParticipantInfo {
  player: Player
  nation: {id: number} | null
  government: {type: GovernmentType} | null
  units: GameUnit[]
  unitLevel: Record<string, number>
  technologies: Technology[]
  availableTechnologies: Technology[]
}

export interface PortalBattleOpponent {
  player: GameSessionPlayer
  armyCount: number
  handCount: number
}

export interface PortalBattle {
  status: BattleStatus
  activePlayer: GameSessionPlayer
  myInfo: BattleParticipant
  opponentInfo: PortalBattleOpponent
  fronts: BattleFront[]
  logEntries: BattleLogEntry[]
  winner: GameSessionPlayer | null
}

export class PortalWorkflowCreateRequest {
  constructor(
    public name: string,
    public gameId: number,
    public ruleSetId: number,
    public nationId: number,
  ) {}
}

export class PortalJoinRequest {
  constructor(public nationId: number) {}
}
