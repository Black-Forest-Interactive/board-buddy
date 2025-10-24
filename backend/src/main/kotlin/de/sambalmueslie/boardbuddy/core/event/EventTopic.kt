package de.sambalmueslie.boardbuddy.core.event

import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.event.api.EventSender
import kotlin.reflect.KClass

internal class EventTopic<T : Any>(
    val type: KClass<T>
) : EventSender<T> {
    private val consumers = mutableSetOf<EventConsumer<T>>()

    fun register(consumer: EventConsumer<T>) {
        consumers.add(consumer)
    }

    fun unregister(consumer: EventConsumer<T>) {
        consumers.remove(consumer)
    }

    override fun created(obj: T) {
        notify { it.created(obj) }
    }

    override fun updated(obj: T) {
        notify { it.updated(obj) }
    }

    override fun deleted(obj: T) {
        notify { it.deleted(obj) }
    }

    private fun notify(action: (EventConsumer<T>) -> (Unit)) {
        consumers.forEach { h -> action.invoke(h) }
    }


}