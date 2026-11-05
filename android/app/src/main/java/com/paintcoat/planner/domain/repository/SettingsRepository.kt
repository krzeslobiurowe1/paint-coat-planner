package com.paintcoat.planner.domain.repository

import com.paintcoat.planner.domain.model.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>

    fun update(settings: AppSettings)

    fun reset()
}
