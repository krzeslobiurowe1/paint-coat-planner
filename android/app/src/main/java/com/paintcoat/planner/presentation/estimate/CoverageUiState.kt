package com.paintcoat.planner.presentation.estimate

import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.Units

data class CoverageChip(
    val valueM2PerLitre: Double,
    val label: String
)

data class CoverageUiState(
    val roomName: String = "",
    val coverageValue: String = "10.0",
    val coats: Int = 2,
    val texture: Texture = Texture.SMOOTH,
    val textureFactorValue: String = "1.00",
    val primer: Boolean = false,
    val estimateLitres: Float = 0f,
    val estimateValue: String = "0.0",
    val chips: List<CoverageChip> = emptyList(),
    val units: Units = Units.METRIC,
    val canDecrease: Boolean = true,
    val canIncrease: Boolean = true
)
