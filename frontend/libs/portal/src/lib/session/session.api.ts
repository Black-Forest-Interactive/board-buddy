export class PortalJoinSessionRequest {
  constructor(public nationId: number) {}
}

export class PortalCreateSessionRequest {
  constructor(
    public name: string,
    public gameId: number,
    public ruleSetId: number,
    public nationId: number,
  ) {}
}
