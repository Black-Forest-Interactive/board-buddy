package de.sambalmueslie.boardbuddy.core.nation.api

import de.sambalmueslie.boardbuddy.engine.api.GovernmentType

sealed interface NationEffect {
    data class InitialGovernment(
        val type: GovernmentType,
    ) : NationEffect
}