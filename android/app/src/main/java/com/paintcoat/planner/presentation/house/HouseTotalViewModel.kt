package com.paintcoat.planner.presentation.house

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcoat.planner.domain.model.AppSettings
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.repository.RoomRepository
import com.paintcoat.planner.domain.repository.SettingsRepository
import com.paintcoat.planner.domain.usecase.ConvertUnitsUseCase
import com.paintcoat.planner.domain.usecase.HouseTotalUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HouseTotalViewModel(
    roomRepository: RoomRepository,
    settingsRepository: SettingsRepository,
    private val houseTotal: HouseTotalUseCase,
    private val convert: ConvertUnitsUseCase
) : ViewModel() {

    val state: StateFlow<HouseTotalUiState> = combine(
        roomRepository.rooms,
        settingsRepository.settings
    ) { rooms, settings ->
        build(rooms, settings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = build(roomRepository.rooms.value, settingsRepository.settings.value)
    )

    private fun build(rooms: List<Room>, settings: AppSettings): HouseTotalUiState {
        val units = settings.units
        val total = houseTotal(rooms, settings.primerCoverage)
        return HouseTotalUiState(
            rooms = total.rooms.map { (room, estimate) ->
                HouseRoomRow(
                    id = room.id,
                    name = room.name,
                    accentHex = room.accentHex,
                    areaValue = convert.areaText(estimate.netAreaM2, units),
                    litresValue = convert.volumeText(estimate.totalLitres, units)
                )
            },
            totalValue = convert.volumeText(total.totalLitres, units),
            hoursValue = convert.decimals(total.totalHours, 1),
            areaValue = convert.areaText(total.totalAreaM2, units),
            roomCount = rooms.size,
            cans = total.cans.map { HouseCanRow(convert.decimals(it.sizeLitres, 1), it.count) },
            units = units
        )
    }
}
