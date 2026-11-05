package com.paintcoat.planner.domain.model

data class Opening(
    val id: String,
    val label: String,
    val widthM: Double,
    val heightM: Double
) {
    val areaM2: Double get() = widthM * heightM
}

data class OpeningPreset(
    val key: String,
    val widthM: Double,
    val heightM: Double
)

object OpeningKeys {
    const val DOOR = "door"
    const val WINDOW = "window"
    const val SMALL_WINDOW = "small_window"
    const val PATIO = "patio"
}

object OpeningPresets {
    val all = listOf(
        OpeningPreset(OpeningKeys.DOOR, 0.9, 2.0),
        OpeningPreset(OpeningKeys.WINDOW, 1.2, 1.2),
        OpeningPreset(OpeningKeys.SMALL_WINDOW, 0.6, 0.9),
        OpeningPreset(OpeningKeys.PATIO, 1.8, 2.1)
    )
}
