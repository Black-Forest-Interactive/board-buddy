import {UnitDefinition} from '../unit/unit.api'
import {Technology} from '../technology/technology.api'

export interface RuleSet {
  id: number
  name: string
  unitDefinitions: UnitDefinition[]
  technologies: Technology[]
}

export class RuleSetChangeRequest {
  constructor(
    public name: string,
  ) {}
}
