package de.sambalmueslie.boardbuddy.gateway.portal.security

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.cookie.Cookie
import io.micronaut.http.cookie.SameSite
import jakarta.inject.Singleton
import java.time.Duration

@Singleton
class PortalPlayerCookie(
    @Value("\${board-buddy.portal.cookie-secure:true}") private val secure: Boolean,
) {
    companion object {
        const val NAME = "bb-player"
        private const val PATH = "/api"
        private val MAX_AGE: Duration = Duration.ofDays(365)
    }

    fun read(request: HttpRequest<*>): String? = request.cookies.findCookie(NAME).map { it.value }.orElse(null)

    fun create(token: String): Cookie = base(token).maxAge(MAX_AGE)

    fun clear(): Cookie = base("").maxAge(0)

    private fun base(value: String): Cookie = Cookie.of(NAME, value)
        .httpOnly(true)
        .secure(secure)
        .sameSite(SameSite.Lax)
        .path(PATH)
}
