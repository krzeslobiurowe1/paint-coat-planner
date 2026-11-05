package com.paintcoat.planner.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.paintcoat.planner.presentation.app.AppViewModel
import com.paintcoat.planner.presentation.estimate.CoverageViewModel
import com.paintcoat.planner.presentation.game.GameViewModel
import com.paintcoat.planner.presentation.gameover.GameOverViewModel
import com.paintcoat.planner.presentation.house.HouseTotalViewModel
import com.paintcoat.planner.presentation.menu.MenuViewModel
import com.paintcoat.planner.presentation.settings.SettingsViewModel
import com.paintcoat.planner.presentation.splash.SplashViewModel

class AppViewModelFactory(private val roomId: String = "") : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val rooms = ServiceLocator.roomRepository
        val settings = ServiceLocator.settingsRepository
        val estimate = ServiceLocator.estimateRoomUseCase
        val house = ServiceLocator.houseTotalUseCase
        val convert = ServiceLocator.convertUnitsUseCase
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(rooms, settings, estimate, convert)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(roomId, rooms, settings, estimate, convert)

            modelClass.isAssignableFrom(CoverageViewModel::class.java) ->
                CoverageViewModel(roomId, rooms, settings, estimate, convert)

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(roomId, rooms, settings, estimate, convert)

            modelClass.isAssignableFrom(HouseTotalViewModel::class.java) ->
                HouseTotalViewModel(rooms, settings, house, convert)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settings, convert)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return requireNotNull(modelClass.cast(created))
    }
}
