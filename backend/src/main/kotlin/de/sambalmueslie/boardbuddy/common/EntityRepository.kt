package de.sambalmueslie.boardbuddy.common

import io.micronaut.data.repository.PageableRepository

interface EntityRepository<E : EntityData> : PageableRepository<E, Long> {
    fun findByIdIn(ids: Set<Long>): List<E>
}