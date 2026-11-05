package com.paintcoat.planner.presentation.gameover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.NumberHero
import com.paintcoat.planner.core.ui.BrushDivider
import com.paintcoat.planner.core.ui.PaintCanGauge
import com.paintcoat.planner.core.ui.PrimaryButton
import com.paintcoat.planner.core.ui.ScreenBackground
import com.paintcoat.planner.core.ui.SecondaryButton
import com.paintcoat.planner.core.ui.SectionHeader
import com.paintcoat.planner.core.ui.StatCard
import com.paintcoat.planner.core.ui.StatItem
import com.paintcoat.planner.core.ui.StatStrip
import com.paintcoat.planner.core.ui.SwatchCard
import com.paintcoat.planner.core.ui.areaText
import com.paintcoat.planner.core.ui.hoursText
import com.paintcoat.planner.core.ui.volumeText

@Composable
fun GameOverScreen(
    roomId: String,
    onPlanAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(
        key = "gameover_" + roomId,
        factory = AppViewModelFactory(roomId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    GameOverContent(
        state = state,
        onPlanAgain = onPlanAgain,
        onMenu = {
            viewModel.save()
            onMenu()
        }
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onPlanAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val colors = LocalAppColors.current
    val haptics = LocalHapticFeedback.current
    val overlay = Brush.verticalGradient(
        0f to colors.paper.copy(alpha = 0.96f),
        1f to colors.surfaceAlt.copy(alpha = 0.98f)
    )
    val decor = Brush.verticalGradient(
        listOf(Color.Transparent, colors.amber.copy(alpha = 0.14f))
    )
    val wallPaintLabel = stringResource(R.string.label_wall_paint)
    val ceilingLabel = stringResource(R.string.label_ceiling_area)
    val trimLabel = stringResource(R.string.label_trim_paint)
    val primerLabel = stringResource(R.string.label_primer_paint)
    val hoursLabel = stringResource(R.string.label_hours)
    val netAreaLabel = stringResource(R.string.label_net_area)
    val stats = mutableListOf<StatItem>()
    if (state.hasWalls) {
        stats.add(
            StatItem(
                label = wallPaintLabel,
                value = volumeText(state.wallLitresValue, state.units),
                accent = colors.enamel
            )
        )
    }
    if (state.hasCeiling) {
        stats.add(
            StatItem(
                label = ceilingLabel,
                value = volumeText(state.ceilingLitresValue, state.units),
                accent = colors.enamelLight
            )
        )
    }
    if (state.hasTrim) {
        stats.add(
            StatItem(
                label = trimLabel,
                value = volumeText(state.trimLitresValue, state.units),
                accent = colors.success
            )
        )
    }
    if (state.hasPrimer) {
        stats.add(
            StatItem(
                label = primerLabel,
                value = volumeText(state.primerLitresValue, state.units),
                accent = colors.warn
            )
        )
    }
    stats.add(
        StatItem(
            label = hoursLabel,
            value = hoursText(state.hoursValue),
            accent = colors.amberDeep
        )
    )
    stats.add(
        StatItem(
            label = netAreaLabel,
            value = areaText(state.netAreaValue, state.units),
            accent = colors.textSecondary
        )
    )
    val rows = stats.chunked(2)

    ScreenBackground(overlay = overlay, decor = decor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = stringResource(R.string.title_can_summary),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    PaintCanGauge(
                        fillFraction = state.fillFraction,
                        description = stringResource(
                            R.string.fmt_cd_gauge,
                            volumeText(state.totalValue, state.units)
                        ),
                        onFilled = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = volumeText(state.totalValue, state.units),
                            style = NumberHero,
                            color = colors.amberDeep,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                        Text(
                            text = stringResource(R.string.fmt_total_for, state.roomName),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                item {
                    SwatchCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(title = stringResource(R.string.label_shopping_list))
                        Spacer(modifier = Modifier.height(8.dp))
                        state.paintCans.forEach { can ->
                            CanLine(sizeValue = can.sizeValue, count = can.count)
                        }
                        if (state.hasSpare) {
                            Text(
                                text = stringResource(R.string.fmt_spare, state.spareValue),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                        if (state.primerCans.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            BrushDivider()
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.label_primer_paint),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            state.primerCans.forEach { can ->
                                CanLine(sizeValue = can.sizeValue, count = can.count)
                            }
                        }
                    }
                }
                item {
                    SectionHeader(title = stringResource(R.string.label_breakdown))
                }
                items(rows.size) { index ->
                    val row = rows[index]
                    if (row.size >= 2) {
                        StatStrip(items = row)
                    } else {
                        StatCard(
                            label = row[0].label,
                            value = row[0].value,
                            accent = row[0].accent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.cta_play_again),
                    onClick = onPlanAgain
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_menu),
                    onClick = onMenu
                )
            }
        }
    }
}

@Composable
private fun CanLine(sizeValue: String, count: Int) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.fmt_can, count, sizeValue),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary
        )
    }
}
