package de.sambalmueslie.boardbuddy.engine.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleType

interface GameEngineDecisionAPI {
    fun determineStartPlayer(attacker: GameSessionPlayer, defender: GameSessionPlayer, type: BattleType, isWalled: Boolean): GameSessionPlayer
    fun determineAttackerUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>, bonusPoints: Int = 0): List<GameEntity>
    fun determineDefenderUnits(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>, bonusPoints: Int = 0): List<GameEntity>
}