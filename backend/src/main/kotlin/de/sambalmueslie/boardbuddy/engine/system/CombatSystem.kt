package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.math.min

@Singleton
class CombatSystem(    componentModelService: GameComponentModelService,) : GameSystem {

    companion object {
        private val logger = LoggerFactory.getLogger(CombatSystem::class.java)
    }
    private val damageModel = componentModelService.get(Damage::class)
    private val typeModel = componentModelService.get(Type::class)
    private val counterTypeModel = componentModelService.get(CounterType::class)

    fun combat(attacker: CombatParticipant, defender: CombatParticipant): List<CombatAction> {
        val actions = mutableListOf<CombatAction>()
        actions.addAll(applyDamage(attacker, defender))

        val fightBack = isDefenderFightingBack(attacker, defender)
        if (fightBack) actions.addAll(applyDamage(defender, attacker))

        return actions
    }


    private fun applyDamage(attacker: CombatParticipant, defender: CombatParticipant): List<CombatAction> {
        val attackDamage = damageModel.get(attacker.unit) ?: return emptyList()

        val damage = min(defender.currentHealth, attackDamage.amount)
        defender.currentHealth -= damage

        val actions = mutableListOf(
            CombatAction.DamageDealt(attacker.unit, damage),
            CombatAction.DamageTaken(defender.unit, damage),
        )

        if (defender.currentHealth <= 0) {
            actions.add(CombatAction.UnitDestroyed(defender.unit))
        }

        return actions
    }

    private fun isDefenderFightingBack(attacker: CombatParticipant, defender: CombatParticipant): Boolean {
        val health = defender.currentHealth
        val killed = health <= 0
        val attackerCounterType = isAttackerCounterType(attacker, defender)
        return !(killed && attackerCounterType)
    }

    private fun isAttackerCounterType(attacker: CombatParticipant, defender: CombatParticipant): Boolean {
        val attackerCounterType = counterTypeModel.get(attacker.unit) ?: return false
        val defenderType = typeModel.get(defender.unit) ?: return false

        return attackerCounterType.kind == defenderType.kind
    }
}