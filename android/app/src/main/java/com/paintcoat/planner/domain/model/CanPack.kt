package com.paintcoat.planner.domain.model

data class CanPack(
    val sizeLitres: Double,
    val count: Int
) {
    val volumeLitres: Double get() = sizeLitres * count
}
