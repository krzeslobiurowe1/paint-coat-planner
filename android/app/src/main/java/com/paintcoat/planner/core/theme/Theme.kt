package com.paintcoat.planner.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paintcoat.planner.domain.model.ThemeMode

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val mode by ThemeState.mode.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val dark = when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }
    val palette = if (dark) DarkAppColors else LightAppColors
    val scheme = if (dark) {
        darkColorScheme(
            primary = palette.enamel,
            onPrimary = palette.onEnamel,
            primaryContainer = palette.enamelDeep,
            onPrimaryContainer = palette.textPrimary,
            secondary = palette.amber,
            onSecondary = palette.paper,
            background = palette.paper,
            onBackground = palette.textPrimary,
            surface = palette.surfaceCard,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceAlt,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.outline,
            error = palette.warn,
            onError = palette.paper
        )
    } else {
        lightColorScheme(
            primary = palette.enamel,
            onPrimary = palette.onEnamel,
            primaryContainer = palette.enamelLight,
            onPrimaryContainer = palette.onEnamel,
            secondary = palette.amber,
            onSecondary = palette.textPrimary,
            background = palette.paper,
            onBackground = palette.textPrimary,
            surface = palette.surfaceCard,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceAlt,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.outline,
            error = palette.warn,
            onError = palette.paper
        )
    }
    CompositionLocalProvider(LocalAppColors provides palette) {
        MaterialTheme(
            colorScheme = scheme,
            typography = AppTypography,
            content = content
        )
    }
}
