export const GovernmentType = {
  DESPOTISM: 'DESPOTISM',
  ANARCHY: 'ANARCHY',
  REPUBLIC: 'REPUBLIC',
  FEUDALISM: 'FEUDALISM',
  COMMUNISM: 'COMMUNISM',
  FUNDAMENTALISM: 'FUNDAMENTALISM',
  DEMOCRACY: 'DEMOCRACY',
} as const
export type GovernmentType = typeof GovernmentType[keyof typeof GovernmentType]

export interface NationEffect {
  type: GovernmentType
}

export interface Nation {
  id: number
  name: string
  description: string
  imageUrl: string
  effect: NationEffect[]
}

export class NationChangeRequest {
  constructor(
    public name: string,
    public description: string,
    public imageUrl: string,
  ) {}
}

export class NationEffectInitialGovernmentRequest {
  constructor(public type: GovernmentType) {}
}
