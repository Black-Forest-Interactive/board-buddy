package de.sambalmueslie.boardbuddy.gateway.portal.api

import de.sambalmueslie.boardbuddy.engine.api.NationType

data class PortalCreateSessionRequest(
    val name: String,
    val gameId: Long,
    val ruleSetId: Long,
    val nation: NationType,
)
