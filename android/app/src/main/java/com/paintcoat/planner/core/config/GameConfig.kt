package com.paintcoat.planner.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    val CAN_SIZES = listOf(10.0, 5.0, 2.5, 1.0)

    const val DEFAULT_COVERAGE = 10.0
    const val PRIMER_COVERAGE = 8.0
    const val TRIM_COVERAGE = 12.0
    const val TRIM_BAND_M = 0.15

    const val PAINT_RATE_M2_PER_HOUR = 10.0
    const val TRIM_RATE_H_PER_M = 0.04
    const val SETUP_HOURS = 0.5

    const val MIN_WALL_W = 0.5
    const val MAX_WALL_W = 20.0
    const val MIN_WALL_H = 1.5
    const val MAX_WALL_H = 6.0
    const val WALL_STEP = 0.1

    const val MIN_COVERAGE = 4.0
    const val MAX_COVERAGE = 18.0
    const val COVERAGE_STEP = 0.5

    const val MIN_PRIMER_COVERAGE = 4.0
    const val MAX_PRIMER_COVERAGE = 14.0

    const val MIN_CEILING_SIDE = 1.0
    const val MAX_CEILING_SIDE = 20.0

    const val DEFAULT_CEILING_LENGTH = 4.0
    const val DEFAULT_CEILING_WIDTH = 3.5
    const val DEFAULT_COATS = 2

    const val METRES_TO_FEET = 3.28084
    const val SQM_TO_SQFT = 10.7639
    const val LITRES_TO_GALLONS = 0.264172

    const val MAX_ROOM_NAME = 24
}
