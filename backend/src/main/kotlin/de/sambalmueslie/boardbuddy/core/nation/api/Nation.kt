package de.sambalmueslie.boardbuddy.core.nation.api

import de.sambalmueslie.boardbuddy.common.Entity

data class Nation(
    override val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val effect: List<NationEffect>
) : Entity
