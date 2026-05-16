package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton

@Singleton
class BattleConverter(
    private val engine: GameEngine
) {


    internal fun convert(data: BattleData): Battle {
        val participant = data.participant.map { convert(it) }
        val fronts = data.fronts.map { convert(it) }
        val logEntries = data.logEntries.map { convert(it) }
        val activePlayer = data.activePlayer
        return Battle(participant, fronts, logEntries, activePlayer, data.status, data.winner)
    }

    private fun convert(data: BattleParticipantData): BattleParticipant {
        val units = data.units.map { u -> engine.getUnit(u) }
        return BattleParticipant(data.player, data.armyCount, units)
    }

    private fun convert(data: BattleFrontData): BattleFront {
        val units = data.units.map { convert(it) }
        return BattleFront(data.index, units)
    }

    private fun convert(data: BattleFrontUnitData): BattleFrontUnit {
        val unit = engine.getUnit(data.unit)
        return BattleFrontUnit(data.player, unit, data.currentHealth)
    }

    private fun convert(data: BattleLogEntryData): BattleLogEntry {
        return BattleLogEntry(data.player, data.activity, data.actions)
    }
}