package de.sambalmueslie.boardbuddy.core.nation.api

import de.sambalmueslie.boardbuddy.engine.api.GovernmentType

data class NationEffectInitialGovernmentRequest(
    val type: GovernmentType,
)
