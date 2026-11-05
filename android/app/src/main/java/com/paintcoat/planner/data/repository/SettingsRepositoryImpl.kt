package com.paintcoat.planner.data.repository

import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.data.local.PlannerPreferences
import com.paintcoat.planner.domain.model.AppSettings
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.ThemeMode
import com.paintcoat.planner.domain.model.Units
import com.paintcoat.planner.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val preferences: PlannerPreferences
) : SettingsRepository {

    private val _settings = MutableStateFlow(read())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun update(settings: AppSettings) {
        preferences.writeString(PlannerPreferences.KEY_UNITS, settings.units.name)
        preferences.writeDouble(PlannerPreferences.KEY_DEFAULT_COVERAGE, settings.defaultCoverage)
        preferences.writeInt(PlannerPreferences.KEY_DEFAULT_COATS, settings.defaultCoats)
        preferences.writeString(PlannerPreferences.KEY_DEFAULT_TEXTURE, settings.defaultTexture.name)
        preferences.writeDouble(PlannerPreferences.KEY_PRIMER_COVERAGE, settings.primerCoverage)
        preferences.writeString(PlannerPreferences.KEY_THEME_MODE, settings.themeMode.name)
        _settings.value = settings
    }

    override fun reset() {
        preferences.clearSettings()
        _settings.value = read()
    }

    private fun read(): AppSettings = AppSettings(
        units = Units.fromKey(preferences.readString(PlannerPreferences.KEY_UNITS, Units.METRIC.name)),
        defaultCoverage = preferences.readDouble(
            PlannerPreferences.KEY_DEFAULT_COVERAGE,
            GameConfig.DEFAULT_COVERAGE
        ),
        defaultCoats = preferences.readInt(
            PlannerPreferences.KEY_DEFAULT_COATS,
            GameConfig.DEFAULT_COATS
        ),
        defaultTexture = Texture.fromKey(
            preferences.readString(PlannerPreferences.KEY_DEFAULT_TEXTURE, Texture.SMOOTH.name)
        ),
        primerCoverage = preferences.readDouble(
            PlannerPreferences.KEY_PRIMER_COVERAGE,
            GameConfig.PRIMER_COVERAGE
        ),
        themeMode = ThemeMode.fromKey(
            preferences.readString(PlannerPreferences.KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        )
    )
}
