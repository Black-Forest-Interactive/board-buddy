package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.GameComponent

interface GameComponentData<T : GameComponent, SELF : GameComponentData<T, SELF>> {
    fun convert(): T
    fun update(value: SELF): SELF
}