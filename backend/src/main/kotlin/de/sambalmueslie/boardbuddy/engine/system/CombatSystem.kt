package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.math.min

@Singleton
class CombatSystem(
    private val storage: GameEntityStorage,
    componentModelService: GameComponentModelService,
    private val eventService: EventService
) : GameSystem {

    companion object {
        private val logger = LoggerFactory.getLogger(CombatSystem::class.java)
    }

    private val damageModel = componentModelService.get(Damage::class)
    private val healthModel = componentModelService.get(Health::class)
    private val levelModel = componentModelService.get(Level::class)
    private val typeModel = componentModelService.get(Type::class)
    private val counterTypeModel = componentModelService.get(CounterType::class)

    fun combat(attacker: GameEntity, defender: GameEntity) {
        applyDamage(attacker, defender)

        val fightBack = isDefenderFightingBack(attacker, defender)
        if (fightBack) applyDamage(defender, attacker)
    }


    private fun applyDamage(attacker: GameEntity, defender: GameEntity) {
        val attackDamage = damageModel.get(attacker) ?: return
        val defendHealth = healthModel.get(defender) ?: return

        val damage = min(defendHealth.amount, attackDamage.amount)
        defendHealth.amount -= damage
        healthModel.update(defender, defendHealth)
        // TODO send out damage event

        if (defendHealth.amount <= 0) {
            eventService.createSender(GameEntity::class).deleted(defender)
            storage.delete(defender)
            // TODO sent out killed event
        }
    }

    private fun isDefenderFightingBack(attacker: GameEntity, defender: GameEntity): Boolean {
        val health = healthModel.get(defender) ?: return false
        val killed = health.amount <= 0
        val attackerCounterType = isAttackerCounterType(attacker, defender)
        return !(killed && attackerCounterType)
    }

    private fun isAttackerCounterType(attacker: GameEntity, defender: GameEntity): Boolean {
        val attackerCounterType = counterTypeModel.get(attacker) ?: return false
        val defenderType = typeModel.get(defender) ?: return false

        return attackerCounterType.kind == defenderType.kind
    }
}