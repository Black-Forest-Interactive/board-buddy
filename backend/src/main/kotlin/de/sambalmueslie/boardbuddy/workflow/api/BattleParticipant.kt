package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameUnit

data class BattleParticipant(
    val player: GameSessionPlayer,
    val armyCount: Int,
    val units: List<GameUnit>,
)
