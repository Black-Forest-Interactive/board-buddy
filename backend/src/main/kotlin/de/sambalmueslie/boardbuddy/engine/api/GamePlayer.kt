package de.sambalmueslie.boardbuddy.engine.api

import de.sambalmueslie.boardbuddy.core.technology.api.Technology

data class GamePlayer(
    val entity: GameEntity,
    val nation: NationReference? = null,
    val government: Government? = null,
    val unitProgress: UnitProgress? = null,
    val technologies: List<Technology> = emptyList(),
)
