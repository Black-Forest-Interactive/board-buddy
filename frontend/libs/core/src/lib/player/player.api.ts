export interface Player {
  id: number
  name: string
}

export class PlayerChangeRequest {
  constructor(
    public name: string,
  ) {}
}
