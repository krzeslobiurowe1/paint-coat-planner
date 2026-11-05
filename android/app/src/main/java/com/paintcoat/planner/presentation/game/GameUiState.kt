package com.paintcoat.planner.presentation.game

import com.paintcoat.planner.domain.model.Units
import com.paintcoat.planner.domain.model.Wall

data class GameWallItem(
    val wall: Wall,
    val position: Int,
    val widthValue: String,
    val heightValue: String,
    val grossValue: String,
    val openingValue: String,
    val netValue: String
)

data class GamePresetItem(
    val key: String,
    val widthValue: String,
    val heightValue: String,
    val count: Int
)

data class GameUiState(
    val roomName: String = "",
    val accentHex: String = "#1F5F8B",
    val wallItems: List<GameWallItem> = emptyList(),
    val maxWallWidthM: Double = 1.0,
    val selectedWallId: String? = null,
    val includeCeiling: Boolean = false,
    val ceilingLengthValue: String = "0.00",
    val ceilingWidthValue: String = "0.00",
    val ceilingAreaValue: String = "0.0",
    val includeTrim: Boolean = false,
    val perimeterValue: String = "0.00",
    val netAreaValue: String = "0.0",
    val wallCount: Int = 0,
    val openingCount: Int = 0,
    val units: Units = Units.METRIC,
    val showAddWall: Boolean = false,
    val addWallWidthValue: String = "0.00",
    val addWallHeightValue: String = "0.00",
    val addWallAreaValue: String = "0.0",
    val openingFitError: Boolean = false,
    val presets: List<GamePresetItem> = emptyList(),
    val canContinue: Boolean = false
)
