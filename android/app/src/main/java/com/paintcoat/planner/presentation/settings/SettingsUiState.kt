package com.paintcoat.planner.presentation.settings

import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.ThemeMode
import com.paintcoat.planner.domain.model.Units

data class SettingsUiState(
    val units: Units = Units.METRIC,
    val coverageValue: String = "10.0",
    val coats: Int = 2,
    val texture: Texture = Texture.SMOOTH,
    val primerCoverageValue: String = "8.0",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showReset: Boolean = false,
    val canDecreaseCoverage: Boolean = true,
    val canIncreaseCoverage: Boolean = true,
    val canDecreasePrimer: Boolean = true,
    val canIncreasePrimer: Boolean = true
)
