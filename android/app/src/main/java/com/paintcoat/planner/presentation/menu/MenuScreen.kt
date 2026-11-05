package com.paintcoat.planner.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.NumberLarge
import com.paintcoat.planner.core.theme.NumberMedium
import com.paintcoat.planner.core.ui.BrushDivider
import com.paintcoat.planner.core.ui.PrimaryButton
import com.paintcoat.planner.core.ui.ScreenBackground
import com.paintcoat.planner.core.ui.SwatchCard
import com.paintcoat.planner.core.ui.areaText
import com.paintcoat.planner.core.ui.formatOneDecimal
import com.paintcoat.planner.core.ui.parseAccent
import com.paintcoat.planner.core.ui.volumeText
import com.paintcoat.planner.presentation.dialog.ConfirmDialog

@Composable
fun MenuScreen(
    onOpenRoom: (String) -> Unit,
    onHouseTotal: () -> Unit,
    onSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.openRoomEvents.collect { roomId ->
            onOpenRoom(roomId)
        }
    }
    MenuContent(
        state = state,
        onStartPlanning = viewModel::startPlanning,
        onOpenRoom = viewModel::openRoom,
        onAskDelete = viewModel::askDelete,
        onHouseTotal = onHouseTotal,
        onSettings = onSettings
    )
    if (state.pendingDeleteId != null) {
        ConfirmDialog(
            title = stringResource(R.string.dialog_delete_room_title),
            body = stringResource(R.string.dialog_delete_room_body),
            confirmText = stringResource(R.string.cta_delete),
            cancelText = stringResource(R.string.cta_cancel),
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDelete
        )
    }
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStartPlanning: () -> Unit,
    onOpenRoom: (String) -> Unit,
    onAskDelete: (String) -> Unit,
    onHouseTotal: () -> Unit,
    onSettings: () -> Unit
) {
    val colors = LocalAppColors.current
    val overlay = Brush.verticalGradient(
        0f to colors.paper.copy(alpha = 0.94f),
        0.5f to colors.surfaceAlt.copy(alpha = 0.97f),
        1f to colors.swatch.copy(alpha = 0.99f)
    )
    val decor = Brush.radialGradient(
        colors = listOf(colors.amber.copy(alpha = 0.12f), colors.paper.copy(alpha = 0f)),
        radius = 520f
    )
    val animatedLitres by animateFloatAsState(
        targetValue = state.houseLitres,
        animationSpec = tween(350),
        label = "houseLitres"
    )
    ScreenBackground(overlay = overlay, decor = decor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            MenuHeader(onSettings = onSettings)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    StaggeredTile(index = 0) {
                        PrimaryButton(
                            text = stringResource(R.string.cta_start),
                            onClick = onStartPlanning,
                            height = 132.dp,
                            leadingIcon = ImageVector.vectorResource(R.drawable.ic_add)
                        )
                    }
                }
                item {
                    StaggeredTile(index = 1) {
                        HouseTotalTile(
                            litres = volumeText(formatOneDecimal(animatedLitres), state.units),
                            onClick = onHouseTotal
                        )
                    }
                }
                item {
                    StaggeredTile(index = 2) {
                        SavedRoomsTile(count = state.roomCount)
                    }
                }
                items(items = state.rooms, key = { it.id }) { room ->
                    val index = state.rooms.indexOfFirst { it.id == room.id } + 3
                    StaggeredTile(index = index.coerceAtMost(6)) {
                        RoomTile(
                            room = room,
                            areaLabel = areaText(room.areaValue, state.units),
                            litresLabel = volumeText(room.litresValue, state.units),
                            onOpen = { onOpenRoom(room.id) },
                            onDelete = { onAskDelete(room.id) }
                        )
                    }
                }
                if (state.rooms.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyRoomsTile()
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuHeader(onSettings: () -> Unit) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = stringResource(R.string.cd_home),
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSettings, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.cd_settings),
                    tint = colors.enamel,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = stringResource(R.string.menu_subtitle),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        BrushDivider(modifier = Modifier.padding(end = 8.dp))
    }
}

@Composable
private fun StaggeredTile(index: Int, content: @Composable () -> Unit) {
    val visibleState = remember(index) {
        MutableTransitionState(false).apply { targetState = true }
    }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(tween(220, delayMillis = index * 45)) +
            slideInVertically(tween(220, delayMillis = index * 45)) { it / 8 }
    ) {
        content()
    }
}

@Composable
private fun HouseTotalTile(litres: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    SwatchCard(
        modifier = Modifier.height(118.dp),
        container = colors.amberWash,
        onClick = onClick
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_paint_can),
            modifier = Modifier.size(36.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = litres,
            style = NumberLarge,
            color = colors.amberDeep,
            maxLines = 1,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        )
        Text(
            text = stringResource(R.string.label_house_total),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun SavedRoomsTile(count: Int) {
    val colors = LocalAppColors.current
    SwatchCard(modifier = Modifier.height(118.dp)) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.cd_swatches),
            modifier = Modifier.size(36.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.fmt_coats, count),
            style = NumberLarge,
            color = colors.enamel,
            maxLines = 1
        )
        Text(
            text = stringResource(R.string.label_saved_rooms),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun RoomTile(
    room: MenuRoomCard,
    areaLabel: String,
    litresLabel: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalAppColors.current
    val description = stringResource(R.string.fmt_cd_room, room.name, areaLabel, litresLabel)
    SwatchCard(
        modifier = Modifier
            .height(176.dp)
            .semantics { contentDescription = description },
        accent = parseAccent(room.accentHex)
    ) {
        Text(
            text = room.name,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = areaLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = litresLabel,
            style = NumberMedium,
            color = colors.amberDeep,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onOpen, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.cd_edit_room),
                    tint = colors.enamel,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.cd_delete_room),
                    tint = colors.warn,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyRoomsTile() {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .drawBehind {
                drawRoundRect(
                    color = colors.outline,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = stringResource(R.string.cd_brush),
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
                color = colors.textSecondary
            )
        }
    }
}
