export interface PortalPlayer {
  id: number
  name: string
  timestamp: string
}

export class CreatePlayerRequest {
  constructor(public name: string) {}
}
