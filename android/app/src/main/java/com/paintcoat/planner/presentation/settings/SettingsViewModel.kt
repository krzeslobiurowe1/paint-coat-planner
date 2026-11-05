package com.paintcoat.planner.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.core.theme.ThemeState
import com.paintcoat.planner.domain.model.AppSettings
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.ThemeMode
import com.paintcoat.planner.domain.model.Units
import com.paintcoat.planner.domain.repository.SettingsRepository
import com.paintcoat.planner.domain.usecase.ConvertUnitsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val convert: ConvertUnitsUseCase
) : ViewModel() {

    private val resetVisible = MutableStateFlow(false)

    val state: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        resetVisible
    ) { settings, reset ->
        build(settings, reset)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = build(settingsRepository.settings.value, false)
    )

    fun setUnits(units: Units) {
        settingsRepository.update(settingsRepository.settings.value.copy(units = units))
    }

    fun changeCoverage(delta: Double) {
        val current = settingsRepository.settings.value
        val next = round(current.defaultCoverage + delta)
            .coerceIn(GameConfig.MIN_COVERAGE, GameConfig.MAX_COVERAGE)
        settingsRepository.update(current.copy(defaultCoverage = next))
    }

    fun setCoats(coats: Int) {
        val current = settingsRepository.settings.value
        settingsRepository.update(current.copy(defaultCoats = coats.coerceIn(1, 3)))
    }

    fun setTexture(texture: Texture) {
        val current = settingsRepository.settings.value
        settingsRepository.update(current.copy(defaultTexture = texture))
    }

    fun changePrimerCoverage(delta: Double) {
        val current = settingsRepository.settings.value
        val next = round(current.primerCoverage + delta)
            .coerceIn(GameConfig.MIN_PRIMER_COVERAGE, GameConfig.MAX_PRIMER_COVERAGE)
        settingsRepository.update(current.copy(primerCoverage = next))
    }

    fun setThemeMode(mode: ThemeMode) {
        settingsRepository.update(settingsRepository.settings.value.copy(themeMode = mode))
        ThemeState.set(mode)
    }

    fun askReset() {
        resetVisible.value = true
    }

    fun dismissReset() {
        resetVisible.value = false
    }

    fun confirmReset() {
        settingsRepository.reset()
        ThemeState.set(settingsRepository.settings.value.themeMode)
        resetVisible.value = false
    }

    private fun build(settings: AppSettings, reset: Boolean): SettingsUiState = SettingsUiState(
        units = settings.units,
        coverageValue = convert.coverageText(settings.defaultCoverage, settings.units),
        coats = settings.defaultCoats,
        texture = settings.defaultTexture,
        primerCoverageValue = convert.coverageText(settings.primerCoverage, settings.units),
        themeMode = settings.themeMode,
        showReset = reset,
        canDecreaseCoverage = settings.defaultCoverage > GameConfig.MIN_COVERAGE,
        canIncreaseCoverage = settings.defaultCoverage < GameConfig.MAX_COVERAGE,
        canDecreasePrimer = settings.primerCoverage > GameConfig.MIN_PRIMER_COVERAGE,
        canIncreasePrimer = settings.primerCoverage < GameConfig.MAX_PRIMER_COVERAGE
    )

    private fun round(value: Double): Double = Math.round(value * 10.0) / 10.0
}
