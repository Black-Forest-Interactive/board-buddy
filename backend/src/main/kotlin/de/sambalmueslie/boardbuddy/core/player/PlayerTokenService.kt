package de.sambalmueslie.boardbuddy.core.player

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.player.db.PlayerTokenData
import de.sambalmueslie.boardbuddy.core.player.db.PlayerTokenRepository
import jakarta.inject.Singleton
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.HexFormat

/**
 * Issues and resolves the opaque tokens that identify portal players.
 * Only a SHA-256 hash is stored; the plain token is handed out exactly once.
 * The token carries 256 bits of entropy, so an unsalted fast hash is sufficient.
 */
@Singleton
class PlayerTokenService(
    private val repository: PlayerTokenRepository,
    private val playerService: PlayerService,
    private val timeProvider: TimeProvider,
) {
    companion object {
        private const val TOKEN_BYTES = 32
        private const val MAX_TOKEN_LENGTH = 128
        private val random = SecureRandom()
    }

    fun issue(player: Player): String {
        require(player.type == PlayerType.HUMAN) { "Tokens are only issued for human players" }
        val bytes = ByteArray(TOKEN_BYTES).also { random.nextBytes(it) }
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        val data = PlayerTokenData(player.id, hash(token), timeProvider.currentTime())
        if (repository.existsById(player.id)) repository.update(data) else repository.save(data)
        return token
    }

    fun resolve(token: String): Player? {
        if (token.isBlank() || token.length > MAX_TOKEN_LENGTH) return null
        val data = repository.findByTokenHash(hash(token)) ?: return null
        return playerService.get(data.playerId)
    }

    fun revoke(playerId: Long) {
        repository.deleteById(playerId)
    }

    private fun hash(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return HexFormat.of().formatHex(digest)
    }
}
