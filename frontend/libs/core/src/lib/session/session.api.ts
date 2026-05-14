import { Player } from '../player/player.api'
import { Game } from '../game/game.api'
import { RuleSet } from '../rule-set/rule-set.api'

export interface GameSession {
  id: number
  key: string
  name: string
  host: Player
  participants: Player[]
  game: Game
  ruleSet: RuleSet
  timestamp: string
}

export class GameSessionChangeRequest {
  constructor(
    public name: string,
    public host: Player,
    public game: Game,
    public ruleSet: RuleSet,
  ) {}
}
