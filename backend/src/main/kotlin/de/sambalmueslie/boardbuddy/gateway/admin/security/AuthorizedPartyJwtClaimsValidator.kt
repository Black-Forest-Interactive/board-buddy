package de.sambalmueslie.boardbuddy.gateway.admin.security

import io.micronaut.context.annotation.Value
import io.micronaut.security.token.Claims
import io.micronaut.security.token.jwt.validator.GenericJwtClaimsValidator
import jakarta.inject.Singleton

/**
 * Keycloak access tokens carry `aud=account` unless an audience mapper is configured,
 * so the token is bound to the admin client via the authorized party (`azp`) claim instead.
 */
@Singleton
class AuthorizedPartyJwtClaimsValidator<T>(
    @Value("\${board-buddy.admin.authorized-party:board-buddy-admin}") private val authorizedParty: String,
) : GenericJwtClaimsValidator<T> {

    override fun validate(claims: Claims, request: T?): Boolean = claims.get("azp")?.toString() == authorizedParty
}
