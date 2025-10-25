package de.sambalmueslie.boardbuddy.core.common

import kotlin.reflect.KClass

open class RequestValidationException(type: KClass<*>, code: Int, msg: String) : RuntimeException(msg) {
}