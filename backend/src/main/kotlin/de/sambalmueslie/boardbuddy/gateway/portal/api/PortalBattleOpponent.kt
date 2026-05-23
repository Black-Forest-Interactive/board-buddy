package de.sambalmueslie.boardbuddy.gateway.portal.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer

data class PortalBattleOpponent(
    val player: GameSessionPlayer,
    val armyCount: Int,
    val handCount: Int,
)
