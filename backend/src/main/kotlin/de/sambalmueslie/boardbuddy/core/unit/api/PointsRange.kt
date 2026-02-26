package de.sambalmueslie.boardbuddy.core.unit.api

data class PointsRange(
    val min: Int,
    val max: Int,
) {
    fun isWithin(value: Int) = value in min..max
}
