package de.sambalmueslie.boardbuddy.core.common

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.notifyCreate
import de.sambalmueslie.boardbuddy.core.event.notifyDelete
import de.sambalmueslie.boardbuddy.core.event.notifyUpdate
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import kotlin.reflect.KClass

abstract class BaseEntityService<T : Entity, R : EntityChangeRequest, D : EntityData>(
    private val repository: EntityRepository<D>,
    private val eventService: EventService,
    private val type: KClass<T>
) : EntityService<T, R> {

    private val sender = eventService.createSender(type)

    override fun get(id: Long): T? {
        return repository.findByIdOrNull(id)?.let { convert(it) }
    }

    override fun getAll(pageable: Pageable): Page<T> {
        return repository.findAll(pageable).map { convert(it) }
    }

    protected abstract fun convert(data: D): T

    override fun create(request: R): T {
        validate(request)
        val data = createData(request)
        return sender.notifyCreate { convert(repository.save(data)) }
    }

    protected abstract fun createData(request: R): D

    override fun update(id: Long, request: R): T {
        val existing = repository.findByIdOrNull(id) ?: return create(request)
        validate(request)
        val data = updateData(existing, request)
        return sender.notifyUpdate { convert(repository.update(data)) }
    }

    abstract fun updateData(existing: D, request: R): D

    protected abstract fun validate(request: R)

    override fun delete(id: Long) {
        val existing = repository.findByIdOrNull(id) ?: return
        repository.delete(existing)
        sender.notifyDelete { convert(existing) }
    }
}