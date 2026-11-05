package com.paintcoat.planner.domain.usecase

import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.domain.model.CanPack
import kotlin.math.floor

class PackCansUseCase {

    operator fun invoke(litres: Double): List<CanPack> {
        if (litres <= 0.0) return emptyList()
        val counts = linkedMapOf<Double, Int>()
        var remaining = litres
        for (size in GameConfig.CAN_SIZES) {
            val count = floor(remaining / size + EPSILON).toInt()
            if (count > 0) {
                counts[size] = (counts[size] ?: 0) + count
                remaining -= count * size
            }
        }
        if (remaining > EPSILON) {
            val filler = GameConfig.CAN_SIZES.lastOrNull { it >= remaining } ?: GameConfig.CAN_SIZES.last()
            counts[filler] = (counts[filler] ?: 0) + 1
        }
        return GameConfig.CAN_SIZES
            .filter { counts.containsKey(it) }
            .map { CanPack(it, counts.getValue(it)) }
    }

    private companion object {
        const val EPSILON = 1e-6
    }
}
