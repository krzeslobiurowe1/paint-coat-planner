package com.paintcoat.planner.domain.model

data class CoverageSetup(
    val coveragePerLitre: Double,
    val coats: Int,
    val texture: Texture,
    val primer: Boolean
)
