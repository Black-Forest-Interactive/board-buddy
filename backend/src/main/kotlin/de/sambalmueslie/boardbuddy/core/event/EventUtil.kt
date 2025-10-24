package de.sambalmueslie.boardbuddy.core.event

import de.sambalmueslie.boardbuddy.core.event.api.EventSender
import kotlin.reflect.KClass


fun <T : Any> EventService.notifyCreate(type: KClass<T>, saveAction: () -> T): T {
    val result = saveAction.invoke()
    createSender(type).created(result)
    return result
}

fun <T : Any> EventSender<T>.notifyCreate(saveAction: () -> T): T {
    val result = saveAction.invoke()
    created(result)
    return result
}

fun <T : Any> EventSender<T>.notifyUpdate(saveAction: () -> T): T {
    val result = saveAction.invoke()
    updated(result)
    return result
}

fun <T : Any> EventSender<T>.notifyDelete(saveAction: () -> T): T {
    val result = saveAction.invoke()
    deleted(result)
    return result
}