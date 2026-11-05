package com.paintcoat.planner.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val paper: Color,
    val surfaceCard: Color,
    val surfaceAlt: Color,
    val swatch: Color,
    val outline: Color,
    val enamel: Color,
    val enamelDeep: Color,
    val enamelLight: Color,
    val amber: Color,
    val amberDeep: Color,
    val amberWash: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val success: Color,
    val warn: Color,
    val onEnamel: Color
)

val LightAppColors = AppColors(
    paper = Color(0xFFFDFBF7),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFF4F1EA),
    swatch = Color(0xFFD7E3EC),
    outline = Color(0xFFB9CBDA),
    enamel = Color(0xFF1F5F8B),
    enamelDeep = Color(0xFF17496B),
    enamelLight = Color(0xFF2E7CAE),
    amber = Color(0xFFF4B860),
    amberDeep = Color(0xFFB9772A),
    amberWash = Color(0xFFFDF3E2),
    textPrimary = Color(0xFF1C2733),
    textSecondary = Color(0xFF5A6B7A),
    textMuted = Color(0xFF8A99A6),
    success = Color(0xFF3E8E6E),
    warn = Color(0xFFC2602E),
    onEnamel = Color(0xFFFDFBF7)
)

val DarkAppColors = AppColors(
    paper = Color(0xFF0D1826),
    surfaceCard = Color(0xFF15243A),
    surfaceAlt = Color(0xFF1C2E47),
    swatch = Color(0xFF24405E),
    outline = Color(0xFF2B415E),
    enamel = Color(0xFF5FA8D3),
    enamelDeep = Color(0xFF1F5F8B),
    enamelLight = Color(0xFF7CC0E6),
    amber = Color(0xFFF4B860),
    amberDeep = Color(0xFFF4B860),
    amberWash = Color(0xFF2A2415),
    textPrimary = Color(0xFFEAF1F7),
    textSecondary = Color(0xFF9FB3C6),
    textMuted = Color(0xFF6E8299),
    success = Color(0xFF6FC4A1),
    warn = Color(0xFFE08A54),
    onEnamel = Color(0xFF0D1826)
)

val SplashTop = Color(0xFF1F5F8B)
val SplashMid = Color(0xFF17496B)
val SplashBottom = Color(0xFF0E2E45)
val SplashTagline = Color(0xFF9FC5DD)
val SplashTitle = Color(0xFFFDFBF7)
val MeniscusAmber = Color(0xFFFFD79A)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
