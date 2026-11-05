package com.paintcoat.planner.domain.model

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromKey(key: String): ThemeMode = entries.firstOrNull { it.name == key } ?: SYSTEM
    }
}
