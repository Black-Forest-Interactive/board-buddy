package de.sambalmueslie.boardbuddy.core.technology.api

import de.sambalmueslie.boardbuddy.common.Entity

data class Technology(
    override val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val tier: Int,
    val effect: List<TechnologyEffect>
) : Entity
