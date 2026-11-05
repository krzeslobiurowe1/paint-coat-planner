package com.paintcoat.planner.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.config.GameConfig
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.ui.BrushDivider
import com.paintcoat.planner.core.ui.PrimaryButton
import com.paintcoat.planner.core.ui.ScreenBackground
import com.paintcoat.planner.core.ui.SectionHeader
import com.paintcoat.planner.core.ui.StatItem
import com.paintcoat.planner.core.ui.StatStrip
import com.paintcoat.planner.core.ui.StepperField
import com.paintcoat.planner.core.ui.SwatchCard
import com.paintcoat.planner.core.ui.WallRectangle
import com.paintcoat.planner.core.ui.areaText
import com.paintcoat.planner.core.ui.lengthText
import com.paintcoat.planner.core.ui.openingLabel
import com.paintcoat.planner.domain.model.Opening
import com.paintcoat.planner.domain.model.Units
import com.paintcoat.planner.presentation.dialog.AddWallDialog

@Composable
fun GameScreen(
    roomId: String,
    onBack: () -> Unit,
    onContinue: (String) -> Unit
) {
    val viewModel: GameViewModel = viewModel(
        key = "game_" + roomId,
        factory = AppViewModelFactory(roomId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.continueEvents.collect { id ->
            onContinue(id)
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.release() }
    }
    GameContent(
        state = state,
        onBack = onBack,
        onRename = viewModel::rename,
        onSelectWall = viewModel::selectWall,
        onRemoveWall = viewModel::removeWall,
        onRemoveOpening = viewModel::removeOpening,
        onApplyPreset = viewModel::applyPreset,
        onOpenAddWall = viewModel::openAddWall,
        onToggleCeiling = viewModel::toggleCeiling,
        onCeilingLength = viewModel::changeCeilingLength,
        onCeilingWidth = viewModel::changeCeilingWidth,
        onToggleTrim = viewModel::toggleTrim,
        onContinue = viewModel::saveAndContinue
    )
    if (state.showAddWall) {
        AddWallDialog(
            widthText = lengthText(state.addWallWidthValue, state.units),
            heightText = lengthText(state.addWallHeightValue, state.units),
            areaText = areaText(state.addWallAreaValue, state.units),
            onWidthDecrease = { viewModel.changeAddWidth(-GameConfig.WALL_STEP) },
            onWidthIncrease = { viewModel.changeAddWidth(GameConfig.WALL_STEP) },
            onHeightDecrease = { viewModel.changeAddHeight(-GameConfig.WALL_STEP) },
            onHeightIncrease = { viewModel.changeAddHeight(GameConfig.WALL_STEP) },
            onConfirm = viewModel::confirmAddWall,
            onDismiss = viewModel::dismissAddWall
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onRename: (String) -> Unit,
    onSelectWall: (String) -> Unit,
    onRemoveWall: (String) -> Unit,
    onRemoveOpening: (String, String) -> Unit,
    onApplyPreset: (String) -> Unit,
    onOpenAddWall: () -> Unit,
    onToggleCeiling: (Boolean) -> Unit,
    onCeilingLength: (Double) -> Unit,
    onCeilingWidth: (Double) -> Unit,
    onToggleTrim: (Boolean) -> Unit,
    onContinue: () -> Unit
) {
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
            GameHeader(
                name = state.roomName,
                onBack = onBack,
                onRename = onRename,
                onSave = onBack
            )
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val boardWidth = minOf(maxWidth - 32.dp, 380.dp)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.wallItems.isEmpty()) {
                        item {
                            EmptyWalls()
                        }
                    }
                    items(items = state.wallItems, key = { it.wall.id }) { item ->
                        WallCard(
                            item = item,
                            units = state.units,
                            boardWidth = boardWidth,
                            maxWallWidthM = state.maxWallWidthM,
                            selected = item.wall.id == state.selectedWallId,
                            onSelect = { onSelectWall(item.wall.id) },
                            onRemove = { onRemoveWall(item.wall.id) },
                            onRemoveOpening = { openingId ->
                                onRemoveOpening(item.wall.id, openingId)
                            }
                        )
                    }
                    item {
                        AddWallRow(onClick = onOpenAddWall)
                    }
                    item {
                        PresetSection(
                            presets = state.presets,
                            units = state.units,
                            enabled = state.wallItems.isNotEmpty(),
                            showError = state.openingFitError,
                            onApplyPreset = onApplyPreset
                        )
                    }
                    item {
                        ToggleSection(
                            state = state,
                            onToggleCeiling = onToggleCeiling,
                            onCeilingLength = onCeilingLength,
                            onCeilingWidth = onCeilingWidth,
                            onToggleTrim = onToggleTrim
                        )
                    }
                }
            }
            BottomPanel(state = state, onContinue = onContinue)
        }
    }
}

@Composable
private fun GameHeader(
    name: String,
    onBack: () -> Unit,
    onRename: (String) -> Unit,
    onSave: () -> Unit
) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colors.enamel,
                    modifier = Modifier.size(24.dp)
                )
            }
            BasicTextField(
                value = name,
                onValueChange = onRename,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.enamel),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            IconButton(onClick = onSave, modifier = Modifier.size(48.dp)) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_save_exit),
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        BrushDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun WallCard(
    item: GameWallItem,
    units: Units,
    boardWidth: Dp,
    maxWallWidthM: Double,
    selected: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
    onRemoveOpening: (String) -> Unit
) {
    val colors = LocalAppColors.current
    val widthLabel = lengthText(item.widthValue, units)
    val heightLabel = lengthText(item.heightValue, units)
    val grossLabel = areaText(item.grossValue, units)
    val openingLabelText = areaText(item.openingValue, units)
    val netLabel = areaText(item.netValue, units)
    val description = stringResource(
        R.string.fmt_cd_wall,
        item.position,
        widthLabel,
        heightLabel,
        item.wall.openings.size
    )
    SwatchCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.fmt_wall_index, item.position),
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) colors.enamel else colors.textSecondary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.cd_remove_wall),
                    tint = colors.warn,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        WallRectangle(
            wall = item.wall,
            boardWidth = boardWidth,
            maxWallWidthM = maxWallWidthM,
            selected = selected,
            onSelect = onSelect,
            description = description
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (item.wall.openings.isEmpty()) {
                stringResource(R.string.fmt_wall_plain, widthLabel, heightLabel, grossLabel)
            } else {
                stringResource(
                    R.string.fmt_wall_caption,
                    widthLabel,
                    heightLabel,
                    grossLabel,
                    openingLabelText,
                    netLabel
                )
            },
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        if (item.wall.openings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item.wall.openings.forEach { opening ->
                    OpeningChip(
                        opening = opening,
                        onRemove = { onRemoveOpening(opening.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OpeningChip(opening: Opening, onRemove: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(colors.surfaceAlt, RoundedCornerShape(12.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onRemove)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = openingLabel(opening.label),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textPrimary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_close),
            contentDescription = stringResource(R.string.cd_remove_wall),
            tint = colors.warn,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun AddWallRow(onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.surfaceCard, RoundedCornerShape(14.dp))
            .border(1.5.dp, colors.enamel, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_add),
            contentDescription = null,
            tint = colors.enamel,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.cta_add_wall),
            style = MaterialTheme.typography.titleMedium,
            color = colors.enamel
        )
    }
}

@Composable
private fun PresetSection(
    presets: List<GamePresetItem>,
    units: Units,
    enabled: Boolean,
    showError: Boolean,
    onApplyPreset: (String) -> Unit
) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = stringResource(R.string.label_opening_presets))
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                PresetChip(
                    label = stringResource(
                        R.string.fmt_preset,
                        openingLabel(preset.key),
                        lengthText(preset.widthValue, units),
                        lengthText(preset.heightValue, units)
                    ),
                    count = preset.count,
                    enabled = enabled,
                    onClick = { onApplyPreset(preset.key) }
                )
            }
        }
        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.error_opening_fit),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.warn
            )
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    count: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(
                if (enabled) colors.surfaceCard else colors.surfaceAlt,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (enabled) colors.enamel.copy(alpha = 0.5f) else colors.outline,
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) colors.textPrimary else colors.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.fmt_badge_count, count),
                style = MaterialTheme.typography.labelSmall,
                color = colors.amberDeep
            )
        }
    }
}

@Composable
private fun ToggleSection(
    state: GameUiState,
    onToggleCeiling: (Boolean) -> Unit,
    onCeilingLength: (Double) -> Unit,
    onCeilingWidth: (Double) -> Unit,
    onToggleTrim: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ToggleRow(
            label = stringResource(R.string.label_ceiling),
            value = areaText(state.ceilingAreaValue, state.units),
            checked = state.includeCeiling,
            onCheckedChange = onToggleCeiling
        )
        if (state.includeCeiling) {
            SectionHeader(title = stringResource(R.string.label_ceiling_length))
            StepperField(
                value = lengthText(state.ceilingLengthValue, state.units),
                onDecrease = { onCeilingLength(-GameConfig.WALL_STEP) },
                onIncrease = { onCeilingLength(GameConfig.WALL_STEP) }
            )
            SectionHeader(title = stringResource(R.string.label_ceiling_width))
            StepperField(
                value = lengthText(state.ceilingWidthValue, state.units),
                onDecrease = { onCeilingWidth(-GameConfig.WALL_STEP) },
                onIncrease = { onCeilingWidth(GameConfig.WALL_STEP) }
            )
        }
        ToggleRow(
            label = stringResource(R.string.label_trim),
            value = lengthText(state.perimeterValue, state.units),
            checked = state.includeTrim,
            onCheckedChange = onToggleTrim
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    value: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.surfaceCard, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp)
            .semantics { stateDescription = if (checked) onLabel else offLabel },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.paper,
                checkedTrackColor = colors.enamel,
                uncheckedThumbColor = colors.textMuted,
                uncheckedTrackColor = colors.swatch
            )
        )
    }
}

@Composable
private fun EmptyWalls() {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceCard, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.empty_walls_title),
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.empty_walls_body),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun BottomPanel(state: GameUiState, onContinue: () -> Unit) {
    val colors = LocalAppColors.current
    val wallsLabel = stringResource(R.string.label_walls)
    val netAreaLabel = stringResource(R.string.label_net_area)
    val openingsLabel = stringResource(R.string.label_openings)
    val items = mutableListOf<StatItem>()
    if (state.wallCount > 0) {
        items.add(
            StatItem(
                label = wallsLabel,
                value = stringResource(R.string.fmt_coats, state.wallCount),
                accent = colors.enamel
            )
        )
        items.add(
            StatItem(
                label = netAreaLabel,
                value = areaText(state.netAreaValue, state.units),
                accent = colors.success
            )
        )
    }
    if (state.openingCount > 0) {
        items.add(
            StatItem(
                label = openingsLabel,
                value = stringResource(R.string.fmt_coats, state.openingCount),
                accent = colors.amberDeep
            )
        )
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
            Box(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                StatStrip(items = items)
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_continue),
                onClick = onContinue,
                enabled = state.canContinue
            )
        }
    }
}
