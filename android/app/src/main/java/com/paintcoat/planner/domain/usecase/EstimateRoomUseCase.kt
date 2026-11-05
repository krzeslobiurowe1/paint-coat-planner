package com.paintcoat.planner.domain.usecase

import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.model.RoomEstimate
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.round

class EstimateRoomUseCase(private val packCans: PackCansUseCase) {

    operator fun invoke(room: Room, primerCoverage: Double = GameConfig.PRIMER_COVERAGE): RoomEstimate {
        val wallArea = room.walls.sumOf { it.grossAreaM2 }
        val openingArea = room.walls.sumOf { it.openingAreaM2 }
        val netArea = max(0.0, wallArea - openingArea)
        val ceilingArea = room.ceiling?.areaM2 ?: 0.0
        val perimeter = room.walls.sumOf { it.widthM }
        val trimLength = if (room.trim) perimeter else 0.0
        val trimArea = trimLength * GameConfig.TRIM_BAND_M

        val setup = room.setup
        val coverage = setup.coveragePerLitre.coerceAtLeast(GameConfig.MIN_COVERAGE)
        val primerPerLitre = primerCoverage.coerceAtLeast(GameConfig.MIN_PRIMER_COVERAGE)

        val wallLitres = ceilOne(netArea * setup.coats * setup.texture.factor / coverage)
        val ceilingLitres = ceilOne(ceilingArea * setup.coats * setup.texture.factor / coverage)
        val trimLitres = ceilOne(trimArea * setup.coats / GameConfig.TRIM_COVERAGE)
        val primerLitres = if (setup.primer) ceilOne((netArea + ceilingArea) / primerPerLitre) else 0.0

        val paintLitres = roundOne(wallLitres + ceilingLitres + trimLitres)
        val totalLitres = roundOne(paintLitres + primerLitres)

        val hours = roundOne(
            (netArea + ceilingArea) * setup.coats / GameConfig.PAINT_RATE_M2_PER_HOUR +
                trimLength * GameConfig.TRIM_RATE_H_PER_M +
                GameConfig.SETUP_HOURS
        )

        return RoomEstimate(
            netAreaM2 = roundOne(netArea),
            ceilingAreaM2 = roundOne(ceilingArea),
            trimLengthM = roundOne(trimLength),
            wallLitres = wallLitres,
            ceilingLitres = ceilingLitres,
            trimLitres = trimLitres,
            primerLitres = primerLitres,
            paintLitres = paintLitres,
            totalLitres = totalLitres,
            cans = packCans(paintLitres),
            primerCans = packCans(primerLitres),
            hours = hours,
            wallCount = room.walls.size,
            openingCount = room.walls.sumOf { it.openings.size }
        )
    }

    private fun roundOne(value: Double): Double = round(value * 10.0) / 10.0

    private fun ceilOne(value: Double): Double = ceil(value * 10.0 - 1e-9) / 10.0
}
