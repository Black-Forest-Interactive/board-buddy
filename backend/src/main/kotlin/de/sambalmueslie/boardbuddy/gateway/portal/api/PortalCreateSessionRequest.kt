package de.sambalmueslie.boardbuddy.gateway.portal.api

data class PortalCreateSessionRequest(
    val name: String,
    val gameId: Long,
    val ruleSetId: Long,
    val nationId: Long,
)
