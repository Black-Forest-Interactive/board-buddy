import {NationType} from '@board-buddy/core'

export class PortalJoinSessionRequest {
  constructor(public nation: NationType) {}
}

export class PortalCreateSessionRequest {
  constructor(
    public name: string,
    public gameId: number,
    public ruleSetId: number,
    public nation: NationType,
  ) {}
}
