package de.sambalmueslie.boardbuddy.engine.api

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CombatAction.DamageDealt::class, name = "DAMAGE_DEALT"),
    JsonSubTypes.Type(value = CombatAction.DamageTaken::class, name = "DAMAGE_TAKEN"),
    JsonSubTypes.Type(value = CombatAction.UnitDestroyed::class, name = "UNIT_DESTROYED")
)
sealed class CombatAction {
    data class DamageDealt(val unit: GameEntity, val amount: Int) : CombatAction()
    data class DamageTaken(val unit: GameEntity, val amount: Int) : CombatAction()
    data class UnitDestroyed(val unit: GameEntity) : CombatAction()
}
