package de.sambalmueslie.boardbuddy.core.event

import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.event.api.EventSender
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

@Singleton
class EventService {
    companion object {
        private val logger = LoggerFactory.getLogger(EventService::class.java)
    }

    private val topics = mutableListOf<EventTopic<*>>()


    fun <T : Any> createSender(type: KClass<T>): EventSender<T> {
        return getOrCreateTopic(type)
    }

    fun <T : Any> register(type: KClass<T>, consumer: EventConsumer<T>) {
        val topic = getOrCreateTopic(type)
        topic.register(consumer)
    }

    fun <T : Any> unregister(type: KClass<T>, consumer: EventConsumer<T>) {
        val topic = getTopic(type) ?: return
        topic.unregister(consumer)
    }

    private fun <T : Any> getOrCreateTopic(type: KClass<T>): EventTopic<T> {
        val existing = getTopic(type)
        if (existing != null) return existing

        return createNewTopic(type)
    }

    private fun <T : Any> createNewTopic(type: KClass<T>): EventTopic<T> {
        val topic = EventTopic(type)
        topics.add(topic)
        return topic
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getTopic(type: KClass<T>): EventTopic<T>? {
        return topics.find { it.type == type } as EventTopic<T>?
    }

}