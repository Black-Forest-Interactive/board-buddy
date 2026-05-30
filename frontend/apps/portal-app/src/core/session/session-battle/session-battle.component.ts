import {Component, input, output} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {TranslatePipe} from '@ngx-translate/core'
import {BattleFront, BattleFrontUnit, GameSessionPlayer, GameUnit, Nation} from '@board-buddy/core'
import {PortalBattle} from '@board-buddy/portal'

@Component({
  selector: 'portal-session-battle',
  imports: [MatButtonModule, MatIconModule, MatCardModule, TranslatePipe],
  templateUrl: './session-battle.component.html',
})
export class SessionBattleComponent {
  readonly battle = input.required<PortalBattle>()
  readonly fronts = input.required<BattleFront[]>()
  readonly reserveUnits = input.required<GameUnit[]>()
  readonly playerId = input.required<number | null>()
  readonly isMyTurn = input.required<boolean>()
  readonly battleFinished = input.required<boolean>()
  readonly battleWinner = input.required<GameSessionPlayer | null>()
  readonly attackableFronts = input.required<Set<number>>()
  readonly isHost = input.required<boolean>()
  readonly selectedUnit = input.required<GameUnit | null>()
  readonly opponentNation = input<Nation | null>(null)

  readonly finishBattle = output()
  readonly cancelBattle = output()
  readonly selectUnit = output<GameUnit>()
  readonly createFront = output<GameUnit>()
  readonly attackFront = output<number>()

  getFrontUnitForMe(front: BattleFront): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id === this.playerId())
  }

  getFrontUnitForOpponent(front: BattleFront): BattleFrontUnit | undefined {
    return front.units.find(fu => fu.player.player.id !== this.playerId())
  }

  hpBoxes(current: number, max: number | null | undefined): boolean[] {
    return Array.from({length: max ?? 0}, (_, i) => i < current)
  }

  range(n: number | null | undefined): number[] {
    return Array.from({length: n ?? 0}, (_, i) => i)
  }

  unitImagePath(kind: string | null | undefined): string | null {
    if (!kind) return null
    const map: Record<string, string> = {INFANTRY: '/img/unit/infantry2_mini.jpg', MOUNTED: '/img/unit/cavalry2_mini.jpg', ARTILLERY: '/img/unit/artillery2_mini.jpg', AIRCRAFT: '/img/unit/plane2_mini.jpg'}
    return map[kind] ?? null
  }
}
