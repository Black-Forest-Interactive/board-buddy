package de.sambalmueslie.boardbuddy.common

import kotlin.reflect.KClass

open class EntityException(type: KClass<*>, code: Int, msg: String) : RuntimeException(msg) {
}