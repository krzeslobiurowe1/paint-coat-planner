package com.paintcoat.planner.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcoat.planner.domain.model.AppSettings
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.repository.RoomRepository
import com.paintcoat.planner.domain.repository.SettingsRepository
import com.paintcoat.planner.domain.usecase.ConvertUnitsUseCase
import com.paintcoat.planner.domain.usecase.EstimateRoomUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.round

class MenuViewModel(
    private val roomRepository: RoomRepository,
    private val settingsRepository: SettingsRepository,
    private val estimateRoom: EstimateRoomUseCase,
    private val convert: ConvertUnitsUseCase
) : ViewModel() {

    private val pendingDelete = MutableStateFlow<String?>(null)

    private val openRoomChannel = Channel<String>(Channel.BUFFERED)
    val openRoomEvents: Flow<String> = openRoomChannel.receiveAsFlow()

    val state: StateFlow<MenuUiState> = combine(
        roomRepository.rooms,
        settingsRepository.settings,
        pendingDelete
    ) { rooms, settings, pending ->
        build(rooms, settings, pending)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = build(
            roomRepository.rooms.value,
            settingsRepository.settings.value,
            null
        )
    )

    fun startPlanning() {
        val room = roomRepository.createRoom()
        viewModelScope.launch {
            openRoomChannel.send(room.id)
        }
    }

    fun openRoom(id: String) {
        viewModelScope.launch {
            openRoomChannel.send(id)
        }
    }

    fun askDelete(id: String) {
        pendingDelete.value = id
    }

    fun dismissDelete() {
        pendingDelete.value = null
    }

    fun confirmDelete() {
        val id = pendingDelete.value ?: return
        roomRepository.deleteRoom(id)
        pendingDelete.value = null
    }

    private fun build(rooms: List<Room>, settings: AppSettings, pending: String?): MenuUiState {
        val units = settings.units
        val estimates = rooms.map { it to estimateRoom(it, settings.primerCoverage) }
        val totalLitres = round(estimates.sumOf { it.second.totalLitres } * 10.0) / 10.0
        return MenuUiState(
            rooms = estimates.map { (room, estimate) ->
                MenuRoomCard(
                    id = room.id,
                    name = room.name,
                    accentHex = room.accentHex,
                    areaValue = convert.areaText(estimate.netAreaM2, units),
                    litresValue = convert.volumeText(estimate.totalLitres, units)
                )
            },
            houseLitresValue = convert.volumeText(totalLitres, units),
            houseLitres = convert.volume(totalLitres, units).toFloat(),
            roomCount = rooms.size,
            units = units,
            pendingDeleteId = pending
        )
    }
}
