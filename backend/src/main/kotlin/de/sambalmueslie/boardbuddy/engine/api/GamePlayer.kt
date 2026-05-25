package de.sambalmueslie.boardbuddy.engine.api

import de.sambalmueslie.boardbuddy.core.technology.api.Technology

data class GamePlayer(
    val entity: GameEntity,
    val nation: Nation? = null,
    val government: Government? = null,
    val technologies: List<Technology> = emptyList(),
)
