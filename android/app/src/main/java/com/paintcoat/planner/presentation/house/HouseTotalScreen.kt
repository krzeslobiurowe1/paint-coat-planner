package com.paintcoat.planner.presentation.house

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.NumberHero
import com.paintcoat.planner.core.theme.NumberMedium
import com.paintcoat.planner.core.ui.BrushDivider
import com.paintcoat.planner.core.ui.ScreenBackground
import com.paintcoat.planner.core.ui.ScreenHeader
import com.paintcoat.planner.core.ui.SecondaryButton
import com.paintcoat.planner.core.ui.SectionHeader
import com.paintcoat.planner.core.ui.SwatchCard
import com.paintcoat.planner.core.ui.areaText
import com.paintcoat.planner.core.ui.parseAccent
import com.paintcoat.planner.core.ui.volumeText
import com.paintcoat.planner.domain.model.Units

@Composable
fun HouseTotalScreen(onBack: () -> Unit) {
    val viewModel: HouseTotalViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()
    HouseTotalContent(state = state, onBack = onBack)
}

@Composable
private fun HouseTotalContent(state: HouseTotalUiState, onBack: () -> Unit) {
    val colors = LocalAppColors.current
    val overlay = Brush.verticalGradient(
        0f to colors.paper.copy(alpha = 0.95f),
        1f to colors.surfaceAlt.copy(alpha = 0.98f)
    )
    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_house_total),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    HeroCard(state = state)
                }
                if (state.rooms.isEmpty()) {
                    item {
                        EmptyHouse()
                    }
                } else {
                    item {
                        SectionHeader(title = stringResource(R.string.label_room_list))
                    }
                    items(items = state.rooms, key = { it.id }) { room ->
                        RoomRow(room = room, units = state.units)
                    }
                    item {
                        SwatchCard(modifier = Modifier.fillMaxWidth()) {
                            SectionHeader(title = stringResource(R.string.label_shopping_list))
                            Spacer(modifier = Modifier.height(8.dp))
                            state.cans.forEach { can ->
                                Text(
                                    text = stringResource(
                                        R.string.fmt_can,
                                        can.count,
                                        can.sizeValue
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
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

@Composable
private fun HeroCard(state: HouseTotalUiState) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .background(colors.amberWash, RoundedCornerShape(20.dp))
            .border(1.dp, colors.amber.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = stringResource(R.string.cd_roller),
            modifier = Modifier.size(44.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = volumeText(state.totalValue, state.units),
                style = NumberHero,
                color = colors.amberDeep,
                maxLines = 1,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Text(
                text = stringResource(R.string.fmt_across_rooms, state.roomCount),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
            Text(
                text = stringResource(R.string.fmt_hours_estimated, state.hoursValue),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun RoomRow(room: HouseRoomRow, units: Units) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 40.dp)
                    .background(parseAccent(room.accentHex), RoundedCornerShape(5.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = areaText(room.areaValue, units),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            Text(
                text = volumeText(room.litresValue, units),
                style = NumberMedium,
                color = colors.amberDeep,
                textAlign = TextAlign.End
            )
        }
        BrushDivider()
    }
}

@Composable
private fun EmptyHouse() {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceCard, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.cd_swatches),
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.empty_rooms_title),
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.empty_rooms_body),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
