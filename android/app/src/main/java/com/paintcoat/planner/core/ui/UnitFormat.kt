package com.paintcoat.planner.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paintcoat.planner.R
import com.paintcoat.planner.domain.model.OpeningKeys
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.ThemeMode
import com.paintcoat.planner.domain.model.Units

@Composable
fun areaText(value: String, units: Units): String = stringResource(
    if (units == Units.METRIC) R.string.fmt_area_metric else R.string.fmt_area_imperial,
    value
)

@Composable
fun lengthText(value: String, units: Units): String = stringResource(
    if (units == Units.METRIC) R.string.fmt_length_metric else R.string.fmt_length_imperial,
    value
)

@Composable
fun volumeText(value: String, units: Units): String = stringResource(
    if (units == Units.METRIC) R.string.fmt_volume_metric else R.string.fmt_volume_imperial,
    value
)

@Composable
fun coverageText(value: String, units: Units): String = stringResource(
    if (units == Units.METRIC) R.string.fmt_coverage_metric else R.string.fmt_coverage_imperial,
    value
)

@Composable
fun hoursText(value: String): String = stringResource(R.string.fmt_hours, value)

@Composable
fun openingLabel(key: String): String = when (key) {
    OpeningKeys.DOOR -> stringResource(R.string.preset_door)
    OpeningKeys.WINDOW -> stringResource(R.string.preset_window)
    OpeningKeys.SMALL_WINDOW -> stringResource(R.string.preset_small_window)
    else -> stringResource(R.string.preset_patio)
}

@Composable
fun textureLabel(texture: Texture): String = when (texture) {
    Texture.SMOOTH -> stringResource(R.string.texture_smooth)
    Texture.LIGHT -> stringResource(R.string.texture_light)
    Texture.ROUGH -> stringResource(R.string.texture_rough)
    Texture.BRICK -> stringResource(R.string.texture_brick)
}

@Composable
fun unitsLabel(units: Units): String = when (units) {
    Units.METRIC -> stringResource(R.string.units_metric)
    Units.IMPERIAL -> stringResource(R.string.units_imperial)
}

@Composable
fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
    ThemeMode.DARK -> stringResource(R.string.theme_dark)
    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
}
