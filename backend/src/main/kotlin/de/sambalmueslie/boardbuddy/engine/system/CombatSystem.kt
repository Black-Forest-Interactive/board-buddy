package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.db.GameComponentStorage
import de.sambalmueslie.boardbuddy.engine.db.GameEntityStorage
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.math.min

@Singleton
class CombatSystem(
    private val storage: GameEntityStorage,
    private val componentStorage: GameComponentStorage,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CombatSystem::class.java)
    }

    fun combat(attacker: GameEntity, defender: GameEntity) {
        applyDamage(attacker, defender)

        val fightBack = isDefenderFightingBack(attacker, defender)
        if (fightBack) applyDamage(defender, attacker)
    }


    private fun applyDamage(attacker: GameEntity, defender: GameEntity) {
        val attackDamage = componentStorage.get(attacker, Damage::class) ?: return
        val defendHealth = componentStorage.get(defender, Health::class) ?: return

        val damage = min(defendHealth.amount, attackDamage.amount)
        defendHealth.amount -= damage
        componentStorage.update(defender, Health::class, defendHealth)
        // TODO send out damage event

        if (defendHealth.amount <= 0) {
            storage.delete(defender)
            // TODO sent out killed event
        }
    }

    private fun isDefenderFightingBack(attacker: GameEntity, defender: GameEntity): Boolean {
        val health = componentStorage.get(defender, Health::class) ?: return false
        val killed = health.amount <= 0
        val attackerCounterType = isAttackerCounterType(attacker, defender)
        return !(killed && attackerCounterType)
    }

    private fun isAttackerCounterType(attacker: GameEntity, defender: GameEntity): Boolean {
        val attackerCounterType = componentStorage.get(attacker, CounterType::class) ?: return false
        val defenderType = componentStorage.get(defender, Type::class) ?: return false

        return attackerCounterType.kind == defenderType.kind
    }
}