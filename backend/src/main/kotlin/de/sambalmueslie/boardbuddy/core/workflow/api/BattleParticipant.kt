package de.sambalmueslie.boardbuddy.core.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance

data class BattleParticipant(
    val player: Player,
    val units: List<UnitInstance>,
    val fronts: List<BattleFront>,
)
