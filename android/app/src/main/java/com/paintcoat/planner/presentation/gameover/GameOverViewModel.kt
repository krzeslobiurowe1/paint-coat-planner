package com.paintcoat.planner.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.repository.RoomRepository
import com.paintcoat.planner.domain.repository.SettingsRepository
import com.paintcoat.planner.domain.usecase.ConvertUnitsUseCase
import com.paintcoat.planner.domain.usecase.EstimateRoomUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(
    roomId: String,
    private val roomRepository: RoomRepository,
    private val settingsRepository: SettingsRepository,
    private val estimateRoom: EstimateRoomUseCase,
    private val convert: ConvertUnitsUseCase
) : ViewModel() {

    private val id: String = roomId

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            roomRepository.rooms.collect { refresh() }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { refresh() }
        }
    }

    fun save() {
        val room = currentRoom() ?: return
        roomRepository.saveRoom(room)
    }

    private fun currentRoom(): Room? = roomRepository.findRoom(id)

    private fun refresh() {
        val room = currentRoom() ?: return
        val settings = settingsRepository.settings.value
        val units = settings.units
        val estimate = estimateRoom(room, settings.primerCoverage)
        val canVolume = estimate.cans.sumOf { it.volumeLitres } +
            estimate.primerCans.sumOf { it.volumeLitres }
        val spare = (canVolume - estimate.totalLitres).coerceAtLeast(0.0)
        val fraction = if (canVolume > 0.0) {
            (estimate.totalLitres / canVolume).toFloat().coerceIn(0.08f, 1f)
        } else {
            0f
        }
        _state.update { current ->
            current.copy(
                roomName = room.name,
                totalValue = convert.volumeText(estimate.totalLitres, units),
                fillFraction = fraction,
                paintCans = estimate.cans.map {
                    CanRow(convert.decimals(it.sizeLitres, 1), it.count)
                },
                primerCans = estimate.primerCans.map {
                    CanRow(convert.decimals(it.sizeLitres, 1), it.count)
                },
                spareValue = convert.decimals(spare, 1),
                hasSpare = spare > 0.05,
                wallLitresValue = convert.volumeText(estimate.wallLitres, units),
                ceilingLitresValue = convert.volumeText(estimate.ceilingLitres, units),
                trimLitresValue = convert.volumeText(estimate.trimLitres, units),
                primerLitresValue = convert.volumeText(estimate.primerLitres, units),
                hoursValue = convert.decimals(estimate.hours, 1),
                netAreaValue = convert.areaText(estimate.netAreaM2, units),
                hasWalls = estimate.wallLitres > 0.0,
                hasCeiling = estimate.ceilingLitres > 0.0,
                hasTrim = estimate.trimLitres > 0.0,
                hasPrimer = estimate.primerLitres > 0.0,
                units = units
            )
        }
    }
}
