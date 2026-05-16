import { RuleSet } from '../rule-set/rule-set.api'

export interface Game {
  id: number
  name: string
  description: string
  ruleSets: RuleSet[]
}

export class GameChangeRequest {
  constructor(
    public name: string,
    public description: string,
  ) {}
}
