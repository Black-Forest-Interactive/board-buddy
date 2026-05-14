import { UnitDefinition } from '../unit/unit.api'

export interface RuleSet {
  id: number
  name: string
  unitDefinitions: UnitDefinition[]
}

export class RuleSetChangeRequest {
  constructor(
    public name: string,
  ) {}
}
