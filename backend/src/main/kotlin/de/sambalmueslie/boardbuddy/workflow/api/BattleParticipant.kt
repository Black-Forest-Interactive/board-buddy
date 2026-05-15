package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class BattleParticipant(
    val player: GameSessionPlayer,
    val armyCount: Int,
    val units: List<GameEntity>,
    val fronts: List<BattleFront>,
)
