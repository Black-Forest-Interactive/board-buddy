package de.sambalmueslie.boardbuddy.engine.api

interface GameComponent {
}

data class Damage(var amount: Int) : GameComponent
data class Health(var amount: Int) : GameComponent
data class Level(var value: Int) : GameComponent
data class Type(val kind: UnitType) : GameComponent
data class CounterType(val kind: UnitType) : GameComponent