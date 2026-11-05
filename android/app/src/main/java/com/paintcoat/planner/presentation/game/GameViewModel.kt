package com.paintcoat.planner.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.data.local.RoomCodec
import com.paintcoat.planner.domain.model.Ceiling
import com.paintcoat.planner.domain.model.Opening
import com.paintcoat.planner.domain.model.OpeningPresets
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.model.Wall
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

class GameViewModel(
    roomId: String,
    private val roomRepository: RoomRepository,
    private val settingsRepository: SettingsRepository,
    private val estimateRoom: EstimateRoomUseCase,
    private val convert: ConvertUnitsUseCase
) : ViewModel() {

    private val id: String = roomRepository.findRoom(roomId)?.id ?: roomRepository.createRoom().id

    private var selectedWallId: String? = null
    private var addWidthM: Double = 3.6
    private var addHeightM: Double = 2.5
    private var addWallVisible: Boolean = false
    private var fitError: Boolean = false
    private var counter: Int = 0

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val continueChannel = Channel<String>(Channel.BUFFERED)
    val continueEvents: Flow<String> = continueChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            roomRepository.rooms.collect { refresh() }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { refresh() }
        }
    }

    fun rename(raw: String) {
        val room = currentRoom() ?: return
        save(room.copy(name = RoomCodec.sanitizeName(raw)))
    }

    fun selectWall(wallId: String) {
        selectedWallId = wallId
        fitError = false
        refresh()
    }

    fun openAddWall() {
        addWallVisible = true
        refresh()
    }

    fun dismissAddWall() {
        addWallVisible = false
        refresh()
    }

    fun changeAddWidth(delta: Double) {
        addWidthM = clamp(addWidthM + delta, GameConfig.MIN_WALL_W, GameConfig.MAX_WALL_W)
        refresh()
    }

    fun changeAddHeight(delta: Double) {
        addHeightM = clamp(addHeightM + delta, GameConfig.MIN_WALL_H, GameConfig.MAX_WALL_H)
        refresh()
    }

    fun confirmAddWall() {
        val room = currentRoom() ?: return
        counter += 1
        val wall = Wall(
            id = "wall_" + counter + "_" + System.currentTimeMillis(),
            widthM = addWidthM,
            heightM = addHeightM
        )
        selectedWallId = wall.id
        addWallVisible = false
        fitError = false
        save(room.copy(walls = room.walls + wall))
    }

    fun removeWall(wallId: String) {
        val room = currentRoom() ?: return
        val remaining = room.walls.filterNot { it.id == wallId }
        if (selectedWallId == wallId) {
            selectedWallId = remaining.firstOrNull()?.id
        }
        save(room.copy(walls = remaining))
    }

    fun applyPreset(key: String) {
        val room = currentRoom() ?: return
        val targetId = selectedWallId ?: room.walls.firstOrNull()?.id ?: return
        val preset = OpeningPresets.all.firstOrNull { it.key == key } ?: return
        val wall = room.walls.firstOrNull { it.id == targetId } ?: return
        val fits = preset.widthM <= wall.widthM &&
            preset.heightM <= wall.heightM &&
            wall.openingAreaM2 + preset.widthM * preset.heightM <= wall.grossAreaM2
        if (!fits) {
            fitError = true
            refresh()
            return
        }
        counter += 1
        val opening = Opening(
            id = "opening_" + counter + "_" + System.currentTimeMillis(),
            label = preset.key,
            widthM = preset.widthM,
            heightM = preset.heightM
        )
        fitError = false
        save(
            room.copy(
                walls = room.walls.map {
                    if (it.id == targetId) it.copy(openings = it.openings + opening) else it
                }
            )
        )
    }

    fun removeOpening(wallId: String, openingId: String) {
        val room = currentRoom() ?: return
        fitError = false
        save(
            room.copy(
                walls = room.walls.map { wall ->
                    if (wall.id == wallId) {
                        wall.copy(openings = wall.openings.filterNot { it.id == openingId })
                    } else {
                        wall
                    }
                }
            )
        )
    }

    fun toggleCeiling(enabled: Boolean) {
        val room = currentRoom() ?: return
        val ceiling = if (enabled) {
            room.ceiling ?: Ceiling(
                GameConfig.DEFAULT_CEILING_LENGTH,
                GameConfig.DEFAULT_CEILING_WIDTH
            )
        } else {
            null
        }
        save(room.copy(ceiling = ceiling))
    }

    fun changeCeilingLength(delta: Double) {
        val room = currentRoom() ?: return
        val ceiling = room.ceiling ?: return
        save(
            room.copy(
                ceiling = ceiling.copy(
                    lengthM = clamp(
                        ceiling.lengthM + delta,
                        GameConfig.MIN_CEILING_SIDE,
                        GameConfig.MAX_CEILING_SIDE
                    )
                )
            )
        )
    }

    fun changeCeilingWidth(delta: Double) {
        val room = currentRoom() ?: return
        val ceiling = room.ceiling ?: return
        save(
            room.copy(
                ceiling = ceiling.copy(
                    widthM = clamp(
                        ceiling.widthM + delta,
                        GameConfig.MIN_CEILING_SIDE,
                        GameConfig.MAX_CEILING_SIDE
                    )
                )
            )
        )
    }

    fun toggleTrim(enabled: Boolean) {
        val room = currentRoom() ?: return
        save(room.copy(trim = enabled))
    }

    fun saveAndContinue() {
        val room = currentRoom() ?: return
        if (room.walls.isEmpty()) return
        save(room)
        viewModelScope.launch {
            continueChannel.send(room.id)
        }
    }

    fun release() {
        addWallVisible = false
        fitError = false
        refresh()
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
        val widest = room.walls.maxOfOrNull { it.widthM } ?: 1.0
        val selected = selectedWallId ?: room.walls.firstOrNull()?.id
        val counts = room.walls
            .firstOrNull { it.id == selected }
            ?.openings
            ?.groupingBy { it.label }
            ?.eachCount()
            .orEmpty()
        val presets = OpeningPresets.all.map { preset ->
            GamePresetItem(
                key = preset.key,
                widthValue = convert.lengthText(preset.widthM, units),
                heightValue = convert.lengthText(preset.heightM, units),
                count = counts[preset.key] ?: 0
            )
        }
        _state.update { current ->
            current.copy(
                roomName = room.name,
                accentHex = room.accentHex,
                wallItems = room.walls.mapIndexed { index, wall ->
                    GameWallItem(
                        wall = wall,
                        position = index + 1,
                        widthValue = convert.lengthText(wall.widthM, units),
                        heightValue = convert.lengthText(wall.heightM, units),
                        grossValue = convert.areaText(wall.grossAreaM2, units),
                        openingValue = convert.areaText(wall.openingAreaM2, units),
                        netValue = convert.areaText(wall.netAreaM2, units)
                    )
                },
                maxWallWidthM = widest,
                selectedWallId = selected,
                includeCeiling = room.ceiling != null,
                ceilingLengthValue = convert.lengthText(room.ceiling?.lengthM ?: 0.0, units),
                ceilingWidthValue = convert.lengthText(room.ceiling?.widthM ?: 0.0, units),
                ceilingAreaValue = convert.areaText(estimate.ceilingAreaM2, units),
                includeTrim = room.trim,
                perimeterValue = convert.lengthText(room.walls.sumOf { it.widthM }, units),
                netAreaValue = convert.areaText(estimate.netAreaM2, units),
                wallCount = room.walls.size,
                openingCount = estimate.openingCount,
                units = units,
                showAddWall = addWallVisible,
                addWallWidthValue = convert.lengthText(addWidthM, units),
                addWallHeightValue = convert.lengthText(addHeightM, units),
                addWallAreaValue = convert.areaText(addWidthM * addHeightM, units),
                openingFitError = fitError,
                presets = presets,
                canContinue = room.walls.isNotEmpty()
            )
        }
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {
        val rounded = Math.round(value * 100.0) / 100.0
        return rounded.coerceIn(min, max)
    }
}
