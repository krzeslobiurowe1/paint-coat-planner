package com.paintcoat.planner.data.sample

import com.paintcoat.planner.domain.model.Ceiling
import com.paintcoat.planner.domain.model.CoverageSetup
import com.paintcoat.planner.domain.model.Opening
import com.paintcoat.planner.domain.model.OpeningKeys
import com.paintcoat.planner.domain.model.Room
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.Wall

object SampleData {

    val accentPalette = listOf(
        "#1F5F8B",
        "#7FA8C4",
        "#F4B860",
        "#C2602E",
        "#3E8E6E",
        "#5A6B7A"
    )

    val newRoomNames = listOf(
        "Living Room",
        "Hallway",
        "Kids Bedroom",
        "Master Bedroom",
        "Kitchen",
        "Bathroom",
        "Study",
        "Dining Room",
        "Landing",
        "Garage"
    )

    fun seedRooms(): List<Room> = listOf(
        Room(
            id = "sample_living_room",
            name = "Living Room",
            accentHex = "#1F5F8B",
            walls = listOf(
                Wall(
                    id = "slr_w1",
                    widthM = 4.2,
                    heightM = 2.5,
                    openings = listOf(Opening("slr_o1", OpeningKeys.DOOR, 0.9, 2.0))
                ),
                Wall(
                    id = "slr_w2",
                    widthM = 3.6,
                    heightM = 2.5,
                    openings = listOf(Opening("slr_o2", OpeningKeys.WINDOW, 1.2, 1.2))
                ),
                Wall(id = "slr_w3", widthM = 4.2, heightM = 2.5),
                Wall(
                    id = "slr_w4",
                    widthM = 3.6,
                    heightM = 2.5,
                    openings = listOf(Opening("slr_o3", OpeningKeys.WINDOW, 1.2, 1.2))
                )
            ),
            ceiling = Ceiling(4.2, 3.6),
            trim = true,
            setup = CoverageSetup(10.0, 2, Texture.SMOOTH, false)
        ),
        Room(
            id = "sample_hallway",
            name = "Hallway",
            accentHex = "#7FA8C4",
            walls = listOf(
                Wall(
                    id = "shw_w1",
                    widthM = 1.4,
                    heightM = 2.5,
                    openings = listOf(Opening("shw_o1", OpeningKeys.DOOR, 0.9, 2.0))
                ),
                Wall(id = "shw_w2", widthM = 5.0, heightM = 2.5),
                Wall(
                    id = "shw_w3",
                    widthM = 1.4,
                    heightM = 2.5,
                    openings = listOf(Opening("shw_o2", OpeningKeys.DOOR, 0.9, 2.0))
                ),
                Wall(id = "shw_w4", widthM = 5.0, heightM = 2.5)
            ),
            ceiling = null,
            trim = true,
            setup = CoverageSetup(10.0, 1, Texture.SMOOTH, false)
        ),
        Room(
            id = "sample_kids_bedroom",
            name = "Kids Bedroom",
            accentHex = "#F4B860",
            walls = listOf(
                Wall(
                    id = "skb_w1",
                    widthM = 3.4,
                    heightM = 2.5,
                    openings = listOf(Opening("skb_o1", OpeningKeys.DOOR, 0.9, 2.0))
                ),
                Wall(id = "skb_w2", widthM = 3.0, heightM = 2.5),
                Wall(
                    id = "skb_w3",
                    widthM = 3.4,
                    heightM = 2.5,
                    openings = listOf(Opening("skb_o2", OpeningKeys.WINDOW, 1.2, 1.2))
                ),
                Wall(id = "skb_w4", widthM = 3.0, heightM = 2.5)
            ),
            ceiling = Ceiling(3.4, 3.0),
            trim = false,
            setup = CoverageSetup(10.0, 2, Texture.LIGHT, true)
        )
    )
}
