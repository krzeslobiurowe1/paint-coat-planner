package com.paintcoat.planner.core.ui

import androidx.compose.ui.graphics.Color

fun parseAccent(hex: String): Color {
    val cleaned = hex.removePrefix("#")
    val value = cleaned.toLongOrNull(16) ?: 0x1F5F8BL
    return Color(0xFF000000L or value)
}
