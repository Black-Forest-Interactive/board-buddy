import {BattleFront, BattleLogEntry, BattleParticipant, BattleStatus, GameSessionPlayer, GameUnit} from '@board-buddy/core'
import {NationType} from '@board-buddy/core'
import {Technology} from '@board-buddy/core'

export interface PortalParticipantInfo {
  player: GameSessionPlayer
  units: GameUnit[]
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
    public nation: NationType,
  ) {}
}

export class PortalJoinRequest {
  constructor(public nation: NationType) {}
}
