package de.sambalmueslie.boardbuddy.gateway.portal.security

import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.AuthorizationException

/**
 * The player id of an authentication created by [PortalPlayerAuthenticationFetcher].
 * Any other authentication (e.g. an admin JWT) is rejected instead of being mapped to a player.
 */
fun Authentication.playerId(): Long =
    (attributes[PortalPlayerAuthenticationFetcher.ATTR_PLAYER_ID] as? Number)?.toLong()
        ?: throw AuthorizationException(this)
