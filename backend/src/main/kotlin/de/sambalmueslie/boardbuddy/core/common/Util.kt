package de.sambalmueslie.boardbuddy.core.common

import io.micronaut.data.repository.CrudRepository

fun <E, ID> CrudRepository<E, ID>.findByIdOrNull(id: ID): E? {
    return findById(id).orElse(null)
}