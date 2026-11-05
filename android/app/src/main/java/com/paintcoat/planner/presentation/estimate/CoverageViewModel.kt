package com.paintcoat.planner.presentation.estimate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.repository.RoomRepository
import com.paintcoat.planner.domain.repository.SettingsRepository
import com.paintcoat.planner.domain.usecase.ConvertUnitsUseCase
import com.paintcoat.planner.domain.usecase.EstimateRoomUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoverageViewModel(
    roomId: String,
    private val roomRepository: RoomRepository,
    private val settingsRepository: SettingsRepository,
    private val estimateRoom: EstimateRoomUseCase,
    private val convert: ConvertUnitsUseCase
) : ViewModel() {

    private val id: String = roomRepository.findRoom(roomId)?.id ?: roomRepository.createRoom().id

    private val _state = MutableStateFlow(CoverageUiState())
    val state: StateFlow<CoverageUiState> = _state.asStateFlow()

    private val calculateChannel = Channel<String>(Channel.BUFFERED)
    val calculateEvents: Flow<String> = calculateChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            roomRepository.rooms.collect { refresh() }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { refresh() }
        }
    }

    fun changeCoverage(delta: Double) {
        val room = currentRoom() ?: return
        setCoverage(room.setup.coveragePerLitre + delta)
    }

    fun setCoverage(value: Double) {
        val room = currentRoom() ?: return
        val clamped = (Math.round(value * 10.0) / 10.0)
            .coerceIn(GameConfig.MIN_COVERAGE, GameConfig.MAX_COVERAGE)
        save(room.copy(setup = room.setup.copy(coveragePerLitre = clamped)))
    }

    fun setCoats(coats: Int) {
        val room = currentRoom() ?: return
        save(room.copy(setup = room.setup.copy(coats = coats.coerceIn(1, 3))))
    }

    fun setTexture(texture: Texture) {
        val room = currentRoom() ?: return
        val needsPrimer = texture == Texture.ROUGH || texture == Texture.BRICK
        save(
            room.copy(
                setup = room.setup.copy(
                    texture = texture,
                    primer = if (needsPrimer) true else room.setup.primer
                )
            )
        )
    }

    fun setPrimer(enabled: Boolean) {
        val room = currentRoom() ?: return
        save(room.copy(setup = room.setup.copy(primer = enabled)))
    }

    fun calculate() {
        val room = currentRoom() ?: return
        save(room)
        viewModelScope.launch {
            calculateChannel.send(room.id)
        }
    }

    private fun currentRoom(): Room? = roomRepository.findRoom(id)

    private fun save(room: Room) {
        roomRepository.saveRoom(room)
        refresh()
    }

    private fun refresh() {
        val room = currentRoom() ?: return
        val settings = settingsRepository.settings.value
        val units = settings.units
        val estimate = estimateRoom(room, settings.primerCoverage)
        _state.update { current ->
            current.copy(
                roomName = room.name,
                coverageValue = convert.coverageText(room.setup.coveragePerLitre, units),
                coats = room.setup.coats,
                texture = room.setup.texture,
                textureFactorValue = convert.decimals(room.setup.texture.factor, 2),
                primer = room.setup.primer,
                estimateLitres = convert.volume(estimate.totalLitres, units).toFloat(),
                estimateValue = convert.volumeText(estimate.totalLitres, units),
                chips = QUICK_VALUES.map { value ->
                    CoverageChip(
                        valueM2PerLitre = value,
                        label = convert.coverageText(value, units)
                    )
                },
                units = units,
                canDecrease = room.setup.coveragePerLitre > GameConfig.MIN_COVERAGE,
                canIncrease = room.setup.coveragePerLitre < GameConfig.MAX_COVERAGE
            )
        }
    }

    private companion object {
        val QUICK_VALUES = listOf(8.0, 10.0, 12.0, 14.0)
    }
}
