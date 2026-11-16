package de.sambalmueslie.boardbuddy.gateway.portal.security

import de.sambalmueslie.boardbuddy.core.player.PlayerTokenService
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.filters.AuthenticationFetcher
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 * Resolves the portal player from the HttpOnly session cookie.
 * Only applies to portal routes and never to requests carrying a bearer token,
 * so player and admin identities cannot mix.
 */
@Singleton
class PortalPlayerAuthenticationFetcher(
    private val tokenService: PlayerTokenService,
    private val cookie: PortalPlayerCookie,
) : AuthenticationFetcher<HttpRequest<*>> {

    companion object {
        const val ROLE = "PORTAL_PLAYER"
        const val ATTR_PLAYER_ID = "portalPlayerId"
        private const val PORTAL_PATH = "/api/portal/"
    }

    override fun fetchAuthentication(request: HttpRequest<*>): Publisher<Authentication> {
        if (!request.path.startsWith(PORTAL_PATH)) return Mono.empty()
        if (request.headers.contains(HttpHeaders.AUTHORIZATION)) return Mono.empty()
        val token = cookie.read(request) ?: return Mono.empty()

        // token lookup is blocking JDBC, keep it off the event loop
        return Mono.fromCallable { tokenService.resolve(token) }
            .subscribeOn(Schedulers.boundedElastic())
            .map { player ->
                Authentication.build(player.id.toString(), listOf(ROLE), mapOf(ATTR_PLAYER_ID to player.id))
            }
    }
}
