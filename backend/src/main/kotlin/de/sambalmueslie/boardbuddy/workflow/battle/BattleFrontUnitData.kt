package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.CombatParticipant
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class BattleFrontUnitData(
    val player: GameSessionPlayer,
    override val unit: GameEntity,
    override var currentHealth: Int,
) : CombatParticipant
