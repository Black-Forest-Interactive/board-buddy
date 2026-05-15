package de.sambalmueslie.boardbuddy.engine.api

sealed class CombatAction {
    data class DamageDealt(val unit: GameEntity, val amount: Int) : CombatAction()
    data class DamageTaken(val unit: GameEntity, val amount: Int) : CombatAction()
    data class UnitDestroyed(val unit: GameEntity) : CombatAction()
}
