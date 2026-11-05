package com.paintcoat.planner.presentation.gameover

import com.paintcoat.planner.domain.model.Units

data class CanRow(
    val sizeValue: String,
    val count: Int
)

data class GameOverUiState(
    val roomName: String = "",
    val totalValue: String = "0.0",
    val fillFraction: Float = 0f,
    val paintCans: List<CanRow> = emptyList(),
    val primerCans: List<CanRow> = emptyList(),
    val spareValue: String = "0.0",
    val hasSpare: Boolean = false,
    val wallLitresValue: String = "0.0",
    val ceilingLitresValue: String = "0.0",
    val trimLitresValue: String = "0.0",
    val primerLitresValue: String = "0.0",
    val hoursValue: String = "0.0",
    val netAreaValue: String = "0.0",
    val hasWalls: Boolean = false,
    val hasCeiling: Boolean = false,
    val hasTrim: Boolean = false,
    val hasPrimer: Boolean = false,
    val units: Units = Units.METRIC
)
