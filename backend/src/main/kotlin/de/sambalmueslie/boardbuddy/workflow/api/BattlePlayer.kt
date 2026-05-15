package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.engine.api.GamePlayer

data class BattlePlayer(
    val player: Player,
    val entity: GamePlayer
)
