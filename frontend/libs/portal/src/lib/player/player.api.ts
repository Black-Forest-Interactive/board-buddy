import {PlayerType} from '@board-buddy/core'

export interface PortalPlayer {
  id: number
  type: PlayerType
  name: string
  timestamp: string
}

export class CreatePlayerRequest {
  constructor(public name: string) {}
}
