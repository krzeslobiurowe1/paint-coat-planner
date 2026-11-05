package com.paintcoat.planner.domain.model

enum class Units {
    METRIC,
    IMPERIAL;

    companion object {
        fun fromKey(key: String): Units = entries.firstOrNull { it.name == key } ?: METRIC
    }
}
