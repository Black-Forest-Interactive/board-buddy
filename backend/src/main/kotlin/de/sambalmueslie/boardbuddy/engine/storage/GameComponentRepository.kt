package de.sambalmueslie.boardbuddy.engine.storage

import io.micronaut.data.repository.PageableRepository

interface GameComponentRepository<D : GameComponentData<*, D>> : PageableRepository<D, Long> {
}