package de.sambalmueslie.boardbuddy.engine.api

import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition

interface GameEngineActionAPI {
    fun createUnit(player: GameSessionPlayer, unitDefinition: UnitDefinition): GameEntity
    fun createPlayer(nation: Nation, unitDefinitions: List<UnitDefinition>): GameEntity
    fun combat(attackingUnit: CombatParticipant, defendingUnit: CombatParticipant): List<CombatAction>
    fun research(session: GameSession, player: GameSessionPlayer, technology: Technology): List<Technology>
}