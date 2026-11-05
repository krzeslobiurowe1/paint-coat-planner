package com.paintcoat.planner.domain.repository

import com.paintcoat.planner.domain.model.Room
import kotlinx.coroutines.flow.StateFlow

interface RoomRepository {
    val rooms: StateFlow<List<Room>>

    fun createRoom(): Room

    fun saveRoom(room: Room)

    fun deleteRoom(id: String)

    fun findRoom(id: String): Room?
}
