package de.sambalmueslie.boardbuddy.core.technology.api

import de.sambalmueslie.boardbuddy.engine.api.UnitType

sealed interface TechnologyEffect {
    data class UnitUnlock(
        val unitType: UnitType,
        val unitLevel: Int,
    ) : TechnologyEffect
}
