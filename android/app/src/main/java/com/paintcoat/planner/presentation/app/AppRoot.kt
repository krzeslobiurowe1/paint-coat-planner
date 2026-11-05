package com.paintcoat.planner.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.navigation.Screen
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.presentation.estimate.CoverageScreen
import com.paintcoat.planner.presentation.game.GameScreen
import com.paintcoat.planner.presentation.gameover.GameOverScreen
import com.paintcoat.planner.presentation.house.HouseTotalScreen
import com.paintcoat.planner.presentation.menu.MenuScreen
import com.paintcoat.planner.presentation.settings.SettingsScreen
import com.paintcoat.planner.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val colors = LocalAppColors.current

    BackHandler(enabled = canGoBack) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.paper)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (targetState is Screen.Game && initialState is Screen.Menu) {
                    (slideInHorizontally(tween(260)) { it / 6 } + fadeIn(tween(260)))
                        .togetherWith(fadeOut(tween(220)))
                } else {
                    fadeIn(tween(320)) togetherWith fadeOut(tween(260))
                }
            },
            label = "rootScreen"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.navigateAsRoot(Screen.Menu) }
                )

                is Screen.Menu -> MenuScreen(
                    onOpenRoom = { roomId -> appViewModel.navigate(Screen.Game(roomId)) },
                    onHouseTotal = { appViewModel.navigate(Screen.HouseTotal) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameScreen(
                    roomId = current.roomId,
                    onBack = { appViewModel.back() },
                    onContinue = { roomId -> appViewModel.navigate(Screen.Coverage(roomId)) }
                )

                is Screen.Coverage -> CoverageScreen(
                    roomId = current.roomId,
                    onBack = { appViewModel.back() },
                    onCalculated = { roomId -> appViewModel.navigate(Screen.GameOver(roomId)) }
                )

                is Screen.GameOver -> GameOverScreen(
                    roomId = current.roomId,
                    onPlanAgain = { appViewModel.navigate(Screen.Game(current.roomId)) },
                    onMenu = { appViewModel.navigateAsRoot(Screen.Menu) }
                )

                is Screen.HouseTotal -> HouseTotalScreen(
                    onBack = { appViewModel.back() }
                )

                is Screen.Settings -> SettingsScreen(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
