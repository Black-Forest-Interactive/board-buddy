package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdFinish
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
 class BattleActionFinish(
    eventService: EventService,
    private val gameEngine: GameEngine,
) : BattleAction<BattleCmdFinish> {

    companion object {
        private val logger = LoggerFactory.getLogger(BattleActionFinish::class.java)
    }

    private val sender = eventService.createSender(GameEntity::class)

    override fun execute(cmd: BattleCmdFinish): BattleData {
        val battle = cmd.battle
        val session = cmd.session
        battle.fronts.flatMap { it.units }
            .filter { it.currentHealth <= 0 }
            .forEach { u ->
                logger.debug("[{}] remove destroyed unit {}", session.id, u.unit)
                sender.deleted(u.unit)
                gameEngine.delete(u.unit)
            }
        return battle
    }
}