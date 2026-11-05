package com.paintcoat.planner.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.ui.ScreenBackground
import com.paintcoat.planner.core.ui.ScreenHeader
import com.paintcoat.planner.core.ui.SecondaryButton
import com.paintcoat.planner.core.ui.SectionHeader
import com.paintcoat.planner.core.ui.SegmentedSelector
import com.paintcoat.planner.core.ui.StepperField
import com.paintcoat.planner.core.ui.SwatchCard
import com.paintcoat.planner.core.ui.coverageText
import com.paintcoat.planner.core.ui.textureLabel
import com.paintcoat.planner.core.ui.themeLabel
import com.paintcoat.planner.core.ui.unitsLabel
import com.paintcoat.planner.domain.model.Texture
import com.paintcoat.planner.domain.model.ThemeMode
import com.paintcoat.planner.domain.model.Units
import com.paintcoat.planner.presentation.dialog.ConfirmDialog

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(
        state = state,
        onBack = onBack,
        onUnits = viewModel::setUnits,
        onCoverage = viewModel::changeCoverage,
        onCoats = viewModel::setCoats,
        onTexture = viewModel::setTexture,
        onPrimerCoverage = viewModel::changePrimerCoverage,
        onTheme = viewModel::setThemeMode,
        onReset = viewModel::askReset
    )
    if (state.showReset) {
        ConfirmDialog(
            title = stringResource(R.string.dialog_reset_title),
            body = stringResource(R.string.dialog_reset_body),
            confirmText = stringResource(R.string.cta_reset),
            cancelText = stringResource(R.string.cta_cancel),
            onConfirm = viewModel::confirmReset,
            onDismiss = viewModel::dismissReset
        )
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onUnits: (Units) -> Unit,
    onCoverage: (Double) -> Unit,
    onCoats: (Int) -> Unit,
    onTexture: (Texture) -> Unit,
    onPrimerCoverage: (Double) -> Unit,
    onTheme: (ThemeMode) -> Unit,
    onReset: () -> Unit
) {
    val colors = LocalAppColors.current
    val overlay = Brush.verticalGradient(
        0f to colors.paper.copy(alpha = 0.95f),
        1f to colors.surfaceAlt.copy(alpha = 0.98f)
    )
    val unitOptions = listOf(Units.METRIC, Units.IMPERIAL)
    val textures = listOf(Texture.SMOOTH, Texture.LIGHT, Texture.ROUGH, Texture.BRICK)
    val themes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)
    val unitOptionLabels = listOf(unitsLabel(Units.METRIC), unitsLabel(Units.IMPERIAL))
    val textureOptionLabels = listOf(
        textureLabel(Texture.SMOOTH),
        textureLabel(Texture.LIGHT),
        textureLabel(Texture.ROUGH),
        textureLabel(Texture.BRICK)
    )
    val themeOptionLabels = listOf(
        themeLabel(ThemeMode.LIGHT),
        themeLabel(ThemeMode.DARK),
        themeLabel(ThemeMode.SYSTEM)
    )
    val coatOptionLabels = listOf(
        stringResource(R.string.fmt_coats, 1),
        stringResource(R.string.fmt_coats, 2),
        stringResource(R.string.fmt_coats, 3)
    )

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_settings),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 4.dp),
                trailing = {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_tape),
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_units))
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedSelector(
                            options = unitOptionLabels,
                            selectedIndex = unitOptions.indexOf(state.units),
                            onSelect = { index -> onUnits(unitOptions[index]) }
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_default_coverage))
                        Spacer(modifier = Modifier.height(10.dp))
                        StepperField(
                            value = coverageText(state.coverageValue, state.units),
                            onDecrease = { onCoverage(-GameConfig.COVERAGE_STEP) },
                            onIncrease = { onCoverage(GameConfig.COVERAGE_STEP) },
                            decreaseEnabled = state.canDecreaseCoverage,
                            increaseEnabled = state.canIncreaseCoverage
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_default_coats))
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedSelector(
                            options = coatOptionLabels,
                            selectedIndex = state.coats - 1,
                            onSelect = { index -> onCoats(index + 1) }
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_default_texture))
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedSelector(
                            options = textureOptionLabels,
                            selectedIndex = textures.indexOf(state.texture),
                            onSelect = { index -> onTexture(textures[index]) }
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_primer_coverage))
                        Spacer(modifier = Modifier.height(10.dp))
                        StepperField(
                            value = coverageText(state.primerCoverageValue, state.units),
                            onDecrease = { onPrimerCoverage(-GameConfig.COVERAGE_STEP) },
                            onIncrease = { onPrimerCoverage(GameConfig.COVERAGE_STEP) },
                            decreaseEnabled = state.canDecreasePrimer,
                            increaseEnabled = state.canIncreasePrimer
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_theme))
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedSelector(
                            options = themeOptionLabels,
                            selectedIndex = themes.indexOf(state.themeMode),
                            onSelect = { index -> onTheme(themes[index]) }
                        )
                    }
                }
                item {
                    SecondaryButton(
                        text = stringResource(R.string.cta_reset),
                        onClick = onReset
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cta_menu),
                    onClick = onBack
                )
            }
        }
    }
}
