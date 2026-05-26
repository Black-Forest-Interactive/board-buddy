package de.sambalmueslie.boardbuddy.engine.api

interface GameComponent {
}

data class Damage(var amount: Int) : GameComponent
data class Health(var amount: Int) : GameComponent
data class Level(var value: Int) : GameComponent
data class Type(val kind: UnitType) : GameComponent
data class CounterType(val kind: UnitType) : GameComponent
data class Government(val type: GovernmentType) : GameComponent
data class NationReference(val id: Long) : GameComponent
data class Technologies(val ids: Set<Long> = emptySet()) : GameComponent
data class UnitProgress(val entries: Map<UnitType, UnitProgressEntry> = emptyMap()) : GameComponent