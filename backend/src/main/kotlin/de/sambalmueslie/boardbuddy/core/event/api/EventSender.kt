package de.sambalmueslie.boardbuddy.core.event.api

interface EventSender<T> {
    fun created(obj: T)
    fun updated(obj: T)
    fun deleted(obj: T)
}