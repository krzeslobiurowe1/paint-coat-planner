package com.paintcoat.planner.domain.usecase

import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.domain.model.Units
import java.util.Locale

class ConvertUnitsUseCase {

    fun length(valueM: Double, units: Units): Double =
        if (units == Units.IMPERIAL) valueM * GameConfig.METRES_TO_FEET else valueM

    fun area(valueM2: Double, units: Units): Double =
        if (units == Units.IMPERIAL) valueM2 * GameConfig.SQM_TO_SQFT else valueM2

    fun volume(litres: Double, units: Units): Double =
        if (units == Units.IMPERIAL) litres * GameConfig.LITRES_TO_GALLONS else litres

    fun coverage(value: Double, units: Units): Double =
        if (units == Units.IMPERIAL) value * GameConfig.SQM_TO_SQFT / GameConfig.LITRES_TO_GALLONS else value

    fun lengthText(valueM: Double, units: Units): String = decimals(length(valueM, units), 2)

    fun areaText(valueM2: Double, units: Units): String = decimals(area(valueM2, units), 1)

    fun volumeText(litres: Double, units: Units): String = decimals(volume(litres, units), 1)

    fun coverageText(value: Double, units: Units): String = decimals(coverage(value, units), 1)

    fun decimals(value: Double, places: Int): String = String.format(Locale.US, "%." + places + "f", value)
}
