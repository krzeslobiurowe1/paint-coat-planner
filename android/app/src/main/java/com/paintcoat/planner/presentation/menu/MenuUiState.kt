package com.paintcoat.planner.presentation.menu

import com.paintcoat.planner.domain.model.Units

data class MenuRoomCard(
    val id: String,
    val name: String,
    val accentHex: String,
    val areaValue: String,
    val litresValue: String
)

data class MenuUiState(
    val rooms: List<MenuRoomCard> = emptyList(),
    val houseLitresValue: String = "0.0",
    val houseLitres: Float = 0f,
    val roomCount: Int = 0,
    val units: Units = Units.METRIC,
    val pendingDeleteId: String? = null
)
