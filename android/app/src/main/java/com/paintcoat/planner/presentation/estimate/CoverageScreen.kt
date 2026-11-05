package com.paintcoat.planner.presentation.estimate

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.NumberLarge
import com.paintcoat.planner.core.ui.PrimaryButton
import com.paintcoat.planner.core.ui.ScreenBackground
import com.paintcoat.planner.core.ui.ScreenHeader
import com.paintcoat.planner.core.ui.SectionHeader
import com.paintcoat.planner.core.ui.SegmentedSelector
import com.paintcoat.planner.core.ui.StepperField
import com.paintcoat.planner.core.ui.SwatchCard
import com.paintcoat.planner.core.ui.coverageText
import com.paintcoat.planner.core.ui.formatOneDecimal
import com.paintcoat.planner.core.ui.textureLabel
import com.paintcoat.planner.core.ui.volumeText
import com.paintcoat.planner.domain.model.Texture

@Composable
fun CoverageScreen(
    roomId: String,
    onBack: () -> Unit,
    onCalculated: (String) -> Unit
) {
    val viewModel: CoverageViewModel = viewModel(
        key = "coverage_" + roomId,
        factory = AppViewModelFactory(roomId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.calculateEvents.collect { id ->
            onCalculated(id)
        }
    }
    CoverageContent(
        state = state,
        onBack = onBack,
        onCoverageDelta = viewModel::changeCoverage,
        onCoverageSet = viewModel::setCoverage,
        onCoats = viewModel::setCoats,
        onTexture = viewModel::setTexture,
        onPrimer = viewModel::setPrimer,
        onCalculate = viewModel::calculate
    )
}

@Composable
private fun CoverageContent(
    state: CoverageUiState,
    onBack: () -> Unit,
    onCoverageDelta: (Double) -> Unit,
    onCoverageSet: (Double) -> Unit,
    onCoats: (Int) -> Unit,
    onTexture: (Texture) -> Unit,
    onPrimer: (Boolean) -> Unit,
    onCalculate: () -> Unit
) {
    val colors = LocalAppColors.current
    val overlay = Brush.verticalGradient(
        0f to colors.paper.copy(alpha = 0.95f),
        1f to colors.surfaceAlt.copy(alpha = 0.98f)
    )
    val decor = Brush.radialGradient(
        colors = listOf(colors.amber.copy(alpha = 0.10f), colors.paper.copy(alpha = 0f)),
        radius = 460f
    )
    val animatedLitres by animateFloatAsState(
        targetValue = state.estimateLitres,
        animationSpec = tween(250),
        label = "coveragePreview"
    )
    val textures = listOf(Texture.SMOOTH, Texture.LIGHT, Texture.ROUGH, Texture.BRICK)
    val textureOptions = listOf(
        textureLabel(Texture.SMOOTH),
        textureLabel(Texture.LIGHT),
        textureLabel(Texture.ROUGH),
        textureLabel(Texture.BRICK)
    )
    val coatOptions = listOf(
        stringResource(R.string.fmt_coats, 1),
        stringResource(R.string.fmt_coats, 2),
        stringResource(R.string.fmt_coats, 3)
    )
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)

    ScreenBackground(overlay = overlay, decor = decor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_coverage),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 4.dp),
                trailing = {
                    Text(
                        text = stringResource(R.string.fmt_step, 2, 3),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                        modifier = Modifier.padding(end = 16.dp)
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
                        SectionHeader(title = stringResource(R.string.label_coverage))
                        Spacer(modifier = Modifier.height(10.dp))
                        StepperField(
                            value = coverageText(state.coverageValue, state.units),
                            onDecrease = { onCoverageDelta(-GameConfig.COVERAGE_STEP) },
                            onIncrease = { onCoverageDelta(GameConfig.COVERAGE_STEP) },
                            decreaseEnabled = state.canDecrease,
                            increaseEnabled = state.canIncrease
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.coverage_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.chips.forEach { chip ->
                                QuickChip(
                                    label = coverageText(chip.label, state.units),
                                    onClick = { onCoverageSet(chip.valueM2PerLitre) }
                                )
                            }
                        }
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_coats))
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedSelector(
                            options = coatOptions,
                            selectedIndex = state.coats - 1,
                            onSelect = { index -> onCoats(index + 1) }
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_texture))
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedSelector(
                            options = textureOptions,
                            selectedIndex = textures.indexOf(state.texture),
                            onSelect = { index -> onTexture(textures[index]) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                R.string.fmt_texture_factor,
                                state.textureFactorValue
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .semantics {
                                    stateDescription = if (state.primer) onLabel else offLabel
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sprite_5),
                                contentDescription = stringResource(R.string.cd_tape),
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.label_primer),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = state.primer,
                                onCheckedChange = onPrimer,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = colors.paper,
                                    checkedTrackColor = colors.enamel,
                                    uncheckedThumbColor = colors.textMuted,
                                    uncheckedTrackColor = colors.swatch
                                )
                            )
                        }
                        Text(
                            text = stringResource(R.string.primer_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = colors.surfaceCard,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.amberWash, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite }
                    ) {
                        Text(
                            text = stringResource(
                                R.string.fmt_estimated,
                                volumeText(formatOneDecimal(animatedLitres), state.units)
                            ),
                            style = NumberLarge,
                            color = colors.amberDeep
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(
                        text = stringResource(R.string.cta_calculate),
                        onClick = onCalculate
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickChip(label: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .height(48.dp)
            .background(colors.surfaceAlt, RoundedCornerShape(12.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textPrimary,
            maxLines = 1
        )
    }
}
