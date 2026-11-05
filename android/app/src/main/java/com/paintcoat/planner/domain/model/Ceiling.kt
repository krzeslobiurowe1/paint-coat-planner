package com.paintcoat.planner.domain.model

data class Ceiling(
    val lengthM: Double,
    val widthM: Double
) {
    val areaM2: Double get() = lengthM * widthM
}
