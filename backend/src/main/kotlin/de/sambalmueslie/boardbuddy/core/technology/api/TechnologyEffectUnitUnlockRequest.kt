package de.sambalmueslie.boardbuddy.core.technology.api

import de.sambalmueslie.boardbuddy.engine.api.UnitType

data class TechnologyEffectUnitUnlockRequest(
    val unitType: UnitType,
    val unitLevel: Int,
)
