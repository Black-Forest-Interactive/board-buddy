export interface Player {
  id: number
  name: string
  timestamp: string
}

export class PlayerChangeRequest {
  constructor(
    public name: string,
  ) {}
}
