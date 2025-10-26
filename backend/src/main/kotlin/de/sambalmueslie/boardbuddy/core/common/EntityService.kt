package de.sambalmueslie.boardbuddy.core.common

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable

interface EntityService<T : Entity, R : EntityChangeRequest> {
    fun get(id: Long): T?
    fun getAll(pageable: Pageable): Page<T>
    fun getByIds(ids: Set<Long>): List<T>

    fun create(request: R): T
    fun update(id: Long, request: R): T
    fun delete(id: Long)
    fun deleteAll()
}