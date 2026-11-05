package com.paintcoat.planner.domain.model

data class AppSettings(
    val units: Units,
    val defaultCoverage: Double,
    val defaultCoats: Int,
    val defaultTexture: Texture,
    val primerCoverage: Double,
    val themeMode: ThemeMode
)
