package de.sambalmueslie.boardbuddy.engine.db

import io.micronaut.data.repository.PageableRepository

interface GameComponentRepository<D : GameComponentData<*>> : PageableRepository<D, Long> {
}