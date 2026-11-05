package com.paintcoat.planner.presentation.house

import com.paintcoat.planner.domain.model.Units

data class HouseRoomRow(
    val id: String,
    val name: String,
    val accentHex: String,
    val areaValue: String,
    val litresValue: String
)

data class HouseCanRow(
    val sizeValue: String,
    val count: Int
)

data class HouseTotalUiState(
    val rooms: List<HouseRoomRow> = emptyList(),
    val totalValue: String = "0.0",
    val hoursValue: String = "0.0",
    val areaValue: String = "0.0",
    val roomCount: Int = 0,
    val cans: List<HouseCanRow> = emptyList(),
    val units: Units = Units.METRIC
)
