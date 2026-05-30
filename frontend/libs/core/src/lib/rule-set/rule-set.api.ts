import {UnitDefinition} from '../unit/unit.api'
import {Technology} from '../technology/technology.api'
import {Nation} from '../nation/nation.api'

export interface RuleSet {
  id: number
  name: string
  unitDefinitions: UnitDefinition[]
  technologies: Technology[]
  nations: Nation[]
}

export class RuleSetChangeRequest {
  constructor(
    public name: string,
  ) {}
}
