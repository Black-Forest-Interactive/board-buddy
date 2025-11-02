package de.sambalmueslie.boardbuddy.core.workflow.api

import de.sambalmueslie.boardbuddy.core.common.EntityException


sealed class WorkflowException(code: Int, msg: String) : EntityException(Workflow::class, code, msg)

private var i = 0

class WorkflowInvalidId(value: String) : WorkflowException(i++, "Invalid id $value")
class WorkflowInvalidHost(value: Long) : WorkflowException(i++, "Invalid host id $value")
class WorkflowInvalidGame(value: Long) : WorkflowException(i++, "Invalid game id $value")
class WorkflowInvalidRuleSet(value: Long) : WorkflowException(i++, "Invalid rule set id $value")
class WorkflowInvalidPlayer(value: Long) : WorkflowException(i++, "Invalid player id $value")
class WorkflowInvalidUnitType(value: Long) : WorkflowException(i++, "Invalid unit type id $value")
class WorkflowPlayerJoinError() : WorkflowException(i++, "Player not able to join")
class WorkflowPlayerActionForbidden(value: Long) : WorkflowException(i++, "Player action not allowed for player $value")
