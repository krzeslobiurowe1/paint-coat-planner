package com.paintcoat.planner.domain.model

data class RoomEstimate(
    val netAreaM2: Double,
    val ceilingAreaM2: Double,
    val trimLengthM: Double,
    val wallLitres: Double,
    val ceilingLitres: Double,
    val trimLitres: Double,
    val primerLitres: Double,
    val paintLitres: Double,
    val totalLitres: Double,
    val cans: List<CanPack>,
    val primerCans: List<CanPack>,
    val hours: Double,
    val wallCount: Int,
    val openingCount: Int
)
