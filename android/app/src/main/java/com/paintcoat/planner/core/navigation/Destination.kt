package com.paintcoat.planner.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(val roomId: String) : Screen

    data class Coverage(val roomId: String) : Screen

    data class GameOver(val roomId: String) : Screen

    data object HouseTotal : Screen

    data object Settings : Screen
}
