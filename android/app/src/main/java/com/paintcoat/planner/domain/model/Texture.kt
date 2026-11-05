package com.paintcoat.planner.domain.model

enum class Texture(val factor: Double) {
    SMOOTH(1.00),
    LIGHT(1.10),
    ROUGH(1.25),
    BRICK(1.45);

    companion object {
        fun fromKey(key: String): Texture = entries.firstOrNull { it.name == key } ?: SMOOTH
    }
}
