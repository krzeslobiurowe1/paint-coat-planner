package com.paintcoat.planner.domain.model

data class Wall(
    val id: String,
    val widthM: Double,
    val heightM: Double,
    val openings: List<Opening> = emptyList()
) {
    val grossAreaM2: Double get() = widthM * heightM
    val openingAreaM2: Double get() = openings.sumOf { it.areaM2 }
    val netAreaM2: Double get() = (grossAreaM2 - openingAreaM2).coerceAtLeast(0.0)
}
