export const PlayerType = {
  HUMAN: 'HUMAN',
  AI: 'AI',
} as const
export type PlayerType = typeof PlayerType[keyof typeof PlayerType]

export interface Player {
  id: number
  type: PlayerType
  name: string
  timestamp: string
}

export class PlayerChangeRequest {
  constructor(
    public type: PlayerType,
    public name: string,
  ) {}
}
