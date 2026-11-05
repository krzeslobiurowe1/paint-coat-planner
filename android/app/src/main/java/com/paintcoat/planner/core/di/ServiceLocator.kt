package com.paintcoat.planner.core.di

import android.content.Context
import com.paintcoat.planner.core.theme.ThemeState
import com.paintcoat.planner.data.local.PlannerPreferences
import com.paintcoat.planner.data.repository.RoomRepositoryImpl
import com.paintcoat.planner.data.repository.SettingsRepositoryImpl
import com.paintcoat.planner.domain.repository.RoomRepository
import com.paintcoat.planner.domain.repository.SettingsRepository
import com.paintcoat.planner.domain.usecase.ConvertUnitsUseCase
import com.paintcoat.planner.domain.usecase.EstimateRoomUseCase
import com.paintcoat.planner.domain.usecase.HouseTotalUseCase
import com.paintcoat.planner.domain.usecase.PackCansUseCase

object ServiceLocator {

    private var roomStore: RoomRepository? = null
    private var settingsStore: SettingsRepository? = null
    private var estimate: EstimateRoomUseCase? = null
    private var house: HouseTotalUseCase? = null
    private var convert: ConvertUnitsUseCase? = null

    fun init(context: Context) {
        if (roomStore != null) return
        val preferences = PlannerPreferences(context.applicationContext)
        val settings = SettingsRepositoryImpl(preferences)
        val rooms = RoomRepositoryImpl(preferences) { settings.settings.value }
        val packCans = PackCansUseCase()
        val estimateRoom = EstimateRoomUseCase(packCans)
        settingsStore = settings
        roomStore = rooms
        estimate = estimateRoom
        house = HouseTotalUseCase(estimateRoom, packCans)
        convert = ConvertUnitsUseCase()
        ThemeState.set(settings.settings.value.themeMode)
    }

    val roomRepository: RoomRepository
        get() = requireNotNull(roomStore)

    val settingsRepository: SettingsRepository
        get() = requireNotNull(settingsStore)

    val estimateRoomUseCase: EstimateRoomUseCase
        get() = requireNotNull(estimate)

    val houseTotalUseCase: HouseTotalUseCase
        get() = requireNotNull(house)

    val convertUnitsUseCase: ConvertUnitsUseCase
        get() = requireNotNull(convert)
}
