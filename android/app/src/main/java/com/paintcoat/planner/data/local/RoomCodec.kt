package com.paintcoat.planner.data.local

import com.paintcoat.planner.domain.model.Ceiling
import com.paintcoat.planner.domain.model.CoverageSetup
import com.paintcoat.planner.domain.model.Opening
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.Wall

object RoomCodec {

    private const val ROOM_SEP = "#"
    private const val FIELD_SEP = "|"
    private const val WALL_SEP = ";"
    private const val WALL_FIELD_SEP = ":"
    private const val OPENING_SEP = ","
    private const val OPENING_FIELD_SEP = "~"
    private const val NONE = "-"

    private val forbidden = charArrayOf('#', '|', ';', ':', ',', '~')

    fun sanitizeName(raw: String): String {
        val cleaned = raw.filterNot { forbidden.contains(it) }
        return cleaned.take(24)
    }

    fun encodeAll(rooms: List<Room>): String = rooms.joinToString(ROOM_SEP) { encode(it) }

    fun decodeAll(raw: String): List<Room> {
        if (raw.isBlank()) return emptyList()
        return raw.split(ROOM_SEP).mapNotNull { decode(it) }
    }

    private fun encode(room: Room): String {
        val walls = room.walls.joinToString(WALL_SEP) { wall ->
            val openings = wall.openings.joinToString(OPENING_SEP) { opening ->
                listOf(opening.id, opening.label, opening.widthM.toString(), opening.heightM.toString())
                    .joinToString(OPENING_FIELD_SEP)
            }
            listOf(
                wall.id,
                wall.widthM.toString(),
                wall.heightM.toString(),
                openings.ifEmpty { NONE }
            ).joinToString(WALL_FIELD_SEP)
        }
        val ceiling = room.ceiling?.let { "${it.lengthM}${WALL_FIELD_SEP}${it.widthM}" } ?: NONE
        val setup = listOf(
            room.setup.coveragePerLitre.toString(),
            room.setup.coats.toString(),
            room.setup.texture.name,
            if (room.setup.primer) "1" else "0"
        ).joinToString(WALL_FIELD_SEP)
        return listOf(
            room.id,
            sanitizeName(room.name),
            room.accentHex,
            walls.ifEmpty { NONE },
            ceiling,
            if (room.trim) "1" else "0",
            setup
        ).joinToString(FIELD_SEP)
    }

    private fun decode(raw: String): Room? {
        val parts = raw.split(FIELD_SEP)
        if (parts.size < 7) return null
        val walls = decodeWalls(parts[3])
        val ceiling = decodeCeiling(parts[4])
        val setup = decodeSetup(parts[6]) ?: return null
        if (parts[0].isBlank()) return null
        return Room(
            id = parts[0],
            name = parts[1],
            accentHex = parts[2],
            walls = walls,
            ceiling = ceiling,
            trim = parts[5] == "1",
            setup = setup
        )
    }

    private fun decodeWalls(raw: String): List<Wall> {
        if (raw == NONE || raw.isBlank()) return emptyList()
        return raw.split(WALL_SEP).mapNotNull { chunk ->
            val fields = chunk.split(WALL_FIELD_SEP)
            if (fields.size < 4) return@mapNotNull null
            val width = fields[1].toDoubleOrNull() ?: return@mapNotNull null
            val height = fields[2].toDoubleOrNull() ?: return@mapNotNull null
            Wall(
                id = fields[0],
                widthM = width,
                heightM = height,
                openings = decodeOpenings(fields[3])
            )
        }
    }

    private fun decodeOpenings(raw: String): List<Opening> {
        if (raw == NONE || raw.isBlank()) return emptyList()
        return raw.split(OPENING_SEP).mapNotNull { chunk ->
            val fields = chunk.split(OPENING_FIELD_SEP)
            if (fields.size < 4) return@mapNotNull null
            val width = fields[2].toDoubleOrNull() ?: return@mapNotNull null
            val height = fields[3].toDoubleOrNull() ?: return@mapNotNull null
            Opening(id = fields[0], label = fields[1], widthM = width, heightM = height)
        }
    }

    private fun decodeCeiling(raw: String): Ceiling? {
        if (raw == NONE || raw.isBlank()) return null
        val fields = raw.split(WALL_FIELD_SEP)
        if (fields.size < 2) return null
        val length = fields[0].toDoubleOrNull() ?: return null
        val width = fields[1].toDoubleOrNull() ?: return null
        return Ceiling(length, width)
    }

    private fun decodeSetup(raw: String): CoverageSetup? {
        val fields = raw.split(WALL_FIELD_SEP)
        if (fields.size < 4) return null
        val coverage = fields[0].toDoubleOrNull() ?: return null
        val coats = fields[1].toIntOrNull() ?: return null
        return CoverageSetup(
            coveragePerLitre = coverage,
            coats = coats,
            texture = Texture.fromKey(fields[2]),
            primer = fields[3] == "1"
        )
    }
}
