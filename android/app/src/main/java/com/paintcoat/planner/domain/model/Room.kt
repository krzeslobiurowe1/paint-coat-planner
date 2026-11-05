package com.paintcoat.planner.domain.model

data class Room(
    val id: String,
    val name: String,
    val accentHex: String,
    val walls: List<Wall>,
    val ceiling: Ceiling?,
    val trim: Boolean,
    val setup: CoverageSetup
)
