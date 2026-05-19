package de.sambalmueslie.boardbuddy.engine.api

import de.sambalmueslie.boardbuddy.common.EntityException
import de.sambalmueslie.boardbuddy.engine.GameEngine


sealed class EngineExceptions(code: Int, msg: String) : EntityException(GameEngine::class, code, msg)

private var i = 0

class WorkflowInvalidGameEntity(value: GameEntity) : EngineExceptions(i++, "Invalid game entity $value")
class EngineResearchAlreadyDiscovered(type: TechnologyType) : EngineExceptions(i++, "Technology already discovered: $type")
class EngineResearchPyramidViolation(type: TechnologyType, required: Int, available: Int) : EngineExceptions(i++, "Cannot research $type: need $required tier-${type.tier - 1} technologies, have $available")