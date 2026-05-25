import {UnitType} from '../unit/unit.api'

export interface TechnologyEffect {
  unitType: UnitType
  unitLevel: number
}

export interface Technology {
  id: number
  name: string
  description: string
  imageUrl: string
  tier: number
  effect: TechnologyEffect[]
}

export class TechnologyChangeRequest {
  constructor(
    public name: string,
    public description: string,
    public imageUrl: string,
    public tier: number,
  ) {}
}

export class TechnologyEffectUnitUnlockRequest {
  constructor(
    public unitType: UnitType,
    public unitLevel: number,
  ) {}
}
