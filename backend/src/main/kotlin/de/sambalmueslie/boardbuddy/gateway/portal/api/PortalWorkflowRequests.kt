package de.sambalmueslie.boardbuddy.gateway.portal.api

// Portal requests never carry the acting player id; it is taken from the authenticated session.

data class PortalCreateUnitRequest(val unitTypeId: Long)

data class PortalResearchRequest(val technologyId: Long)

data class PortalBattleCreateFrontRequest(val entityId: Long)

data class PortalBattleAttackFrontRequest(val entityId: Long, val frontIndex: Int)
