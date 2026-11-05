package com.paintcoat.planner.data.repository

import com.paintcoat.planner.data.local.PlannerPreferences
import com.paintcoat.planner.data.local.RoomCodec
import com.paintcoat.planner.data.sample.SampleData
import com.paintcoat.planner.domain.model.AppSettings
import com.paintcoat.planner.domain.model.CoverageSetup
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RoomRepositoryImpl(
    private val preferences: PlannerPreferences,
    private val settingsProvider: () -> AppSettings
) : RoomRepository {

    private val _rooms = MutableStateFlow(loadInitial())
    override val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    override fun createRoom(): Room {
        val index = preferences.nextRoomIndex()
        val settings = settingsProvider()
        val existing = _rooms.value.map { it.name }
        val name = SampleData.newRoomNames.firstOrNull { !existing.contains(it) }
            ?: SampleData.newRoomNames.first() + " " + index
        val room = Room(
            id = "room_" + index + "_" + System.currentTimeMillis(),
            name = name,
            accentHex = SampleData.accentPalette[index % SampleData.accentPalette.size],
            walls = emptyList(),
            ceiling = null,
            trim = false,
            setup = CoverageSetup(
                coveragePerLitre = settings.defaultCoverage,
                coats = settings.defaultCoats,
                texture = settings.defaultTexture,
                primer = false
            )
        )
        persist(_rooms.value + room)
        return room
    }

    override fun saveRoom(room: Room) {
        val current = _rooms.value
        val index = current.indexOfFirst { it.id == room.id }
        val updated = if (index >= 0) {
            current.toMutableList().also { it[index] = room }
        } else {
            current + room
        }
        persist(updated)
    }

    override fun deleteRoom(id: String) {
        persist(_rooms.value.filterNot { it.id == id })
    }

    override fun findRoom(id: String): Room? = _rooms.value.firstOrNull { it.id == id }

    private fun persist(rooms: List<Room>) {
        _rooms.value = rooms
        preferences.writeRooms(RoomCodec.encodeAll(rooms))
    }

    private fun loadInitial(): List<Room> {
        if (!preferences.isSeeded()) {
            val seeded = SampleData.seedRooms()
            preferences.writeRooms(RoomCodec.encodeAll(seeded))
            preferences.markSeeded()
            return seeded
        }
        return RoomCodec.decodeAll(preferences.readRooms())
    }
}
