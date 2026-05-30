export interface PortalPlayer {
  id: number
  name: string
}

export class CreatePlayerRequest {
  constructor(public name: string) {}
}
