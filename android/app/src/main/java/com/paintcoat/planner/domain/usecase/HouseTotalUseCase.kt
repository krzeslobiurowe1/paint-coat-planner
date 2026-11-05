package com.paintcoat.planner.domain.usecase

import com.paintcoat.planner.domain.model.CanPack
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.model.RoomEstimate
import kotlin.math.round

class HouseTotalUseCase(
    private val estimateRoom: EstimateRoomUseCase,
    private val packCans: PackCansUseCase
) {

    data class HouseTotal(
        val rooms: List<Pair<Room, RoomEstimate>>,
        val totalLitres: Double,
        val totalHours: Double,
        val totalAreaM2: Double,
        val cans: List<CanPack>
    )

    operator fun invoke(rooms: List<Room>, primerCoverage: Double): HouseTotal {
        val pairs = rooms.map { it to estimateRoom(it, primerCoverage) }
        val litres = roundOne(pairs.sumOf { it.second.totalLitres })
        val hours = roundOne(pairs.sumOf { it.second.hours })
        val area = roundOne(pairs.sumOf { it.second.netAreaM2 + it.second.ceilingAreaM2 })
        return HouseTotal(
            rooms = pairs,
            totalLitres = litres,
            totalHours = hours,
            totalAreaM2 = area,
            cans = packCans(litres)
        )
    }

    private fun roundOne(value: Double): Double = round(value * 10.0) / 10.0
}
