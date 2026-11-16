package de.sambalmueslie.boardbuddy.gateway.admin.security

import io.micronaut.context.annotation.Replaces
import io.micronaut.security.token.DefaultRolesFinder
import io.micronaut.security.token.RolesFinder
import io.micronaut.security.token.config.TokenConfiguration
import jakarta.inject.Singleton

/**
 * Keycloak puts realm roles into the nested claim `realm_access.roles`, the default finder only reads the flat `roles` attribute.
 * Both are supported, the flat attribute is still used by the portal player authentication.
 */
@Singleton
@Replaces(DefaultRolesFinder::class)
class KeycloakRolesFinder(private val tokenConfiguration: TokenConfiguration) : RolesFinder {

    companion object {
        private const val REALM_ACCESS = "realm_access"
        private const val REALM_ROLES = "roles"
    }

    override fun resolveRoles(attributes: Map<String, Any>?): List<String> {
        if (attributes == null) return emptyList()
        val flat = toRoles(attributes[tokenConfiguration.rolesName])
        val realm = toRoles((attributes[REALM_ACCESS] as? Map<*, *>)?.get(REALM_ROLES))
        return (flat + realm).distinct()
    }

    private fun toRoles(value: Any?): List<String> = when (value) {
        is Iterable<*> -> value.mapNotNull { it?.toString() }
        is Array<*> -> value.mapNotNull { it?.toString() }
        is String -> listOf(value)
        else -> emptyList()
    }
}
