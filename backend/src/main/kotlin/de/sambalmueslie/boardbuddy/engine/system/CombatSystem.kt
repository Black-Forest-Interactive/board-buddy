package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.engine.ComponentStoreService
import de.sambalmueslie.boardbuddy.engine.GameEntityData
import de.sambalmueslie.boardbuddy.engine.api.CounterType
import de.sambalmueslie.boardbuddy.engine.api.Damage
import de.sambalmueslie.boardbuddy.engine.api.Health
import de.sambalmueslie.boardbuddy.engine.api.Type
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.math.min

@Singleton
class CombatSystem(
    private val storeService: ComponentStoreService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CombatSystem::class.java)
    }

    fun combat(attacker: GameEntityData, defender: GameEntityData) {
        applyDamage(attacker, defender)

        val fightBack = isDefenderFightingBack(attacker, defender)
        if (fightBack) applyDamage(defender, attacker)
    }


    private fun applyDamage(attacker: GameEntityData, defender: GameEntityData) {
        val attackDamage = storeService.get(Damage::class).get(attacker) ?: return
        val defendHealth = storeService.get(Health::class).get(defender) ?: return

        val damage = min(defendHealth.amount, attackDamage.amount)
        defendHealth.amount -= damage
        // TODO send out damage event

        if (defendHealth.amount <= 0) {
            // TODo sent out killed event
        }
    }

    private fun isDefenderFightingBack(attacker: GameEntityData, defender: GameEntityData): Boolean {
        val health = storeService.get(Health::class).get(defender) ?: return false
        val killed = health.amount <= 0
        val attackerCounterType = isAttackerCounterType(attacker, defender)
        return !(killed && attackerCounterType)
    }

    private fun isAttackerCounterType(attacker: GameEntityData, defender: GameEntityData): Boolean {
        val attackerCounterType = storeService.get(CounterType::class).get(attacker) ?: return false
        val defenderType = storeService.get(Type::class).get(defender) ?: return false

        return attackerCounterType.kind == defenderType.kind
    }
}