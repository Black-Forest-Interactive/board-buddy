package de.sambalmueslie.boardbuddy.workflow.battle.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSession

interface WorkflowBattleAPI {
    fun get(session: GameSession): Battle?
    fun start(session: GameSession, request: WorkflowBattleStartRequest): Battle
    fun cancel(session: GameSession): Battle
    fun finish(session: GameSession)

    fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle
    fun attackFront(session: GameSession, request: WorkflowBattleAttackFrontRequest): Battle
}