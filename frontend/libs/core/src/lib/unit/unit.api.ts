export const UnitType = {
  INFANTRY: 'INFANTRY',
  ARTILLERY: 'ARTILLERY',
  CAVALRY: 'CAVALRY',
  PLANE: 'PLANE',
} as const

export type UnitType = typeof UnitType[keyof typeof UnitType]

export class PointsRange {
  constructor(
    public min: number,
    public max: number,
  ) {}
}

export interface UnitDefinition {
  id: number
  name: string
  unitType: UnitType
  counterType: UnitType | undefined
  damagePoints: PointsRange
  healthPoints: PointsRange
  maxLevel: number
}

export class UnitDefinitionChangeRequest {
  constructor(
    public name: string,
    public unitType: UnitType,
    public counterType: UnitType | undefined,
    public damagePoints: PointsRange,
    public healthPoints: PointsRange,
    public maxLevel: number,
  ) {}
}
