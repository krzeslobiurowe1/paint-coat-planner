package com.paintcoat.planner.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.domain.model.Wall

@Composable
fun WallRectangle(
    wall: Wall,
    boardWidth: Dp,
    maxWallWidthM: Double,
    selected: Boolean,
    onSelect: () -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val frame = BOARD_PAD + BOARD_BORDER
    val inner = boardWidth - frame * 2f
    val widest = maxWallWidthM.coerceAtLeast(0.5).toFloat()
    val perMetre = inner / widest
    val rectWidth = (perMetre * wall.widthM.toFloat()).coerceIn(48.dp, inner)
    val rectHeight = (perMetre * wall.heightM.toFloat()).coerceIn(56.dp, 150.dp)
    val heightPerMetre = rectHeight / wall.heightM.toFloat().coerceAtLeast(0.5f)

    val gap = 6.dp
    val rawWidths = wall.openings.map { perMetre * it.widthM.toFloat() }
    val gapsTotal = gap * (wall.openings.size - 1).coerceAtLeast(0).toFloat()
    val rawTotal = rawWidths.fold(0.dp) { acc, value -> acc + value } + gapsTotal
    val available = rectWidth - 12.dp
    val factor = if (rawTotal > available && rawTotal.value > 0f) available.value / rawTotal.value else 1f

    Box(
        modifier = modifier
            .width(rectWidth + frame * 2f)
            .height(rectHeight + frame * 2f)
            .background(colors.swatch, RoundedCornerShape(10.dp))
            .border(
                width = BOARD_BORDER,
                color = if (selected) colors.enamel else colors.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onSelect)
            .semantics { contentDescription = description }
            .padding(frame)
    ) {
        Row(
            modifier = Modifier.align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Bottom
        ) {
            wall.openings.forEachIndexed { index, opening ->
                val visibleState = remember(opening.id) {
                    MutableTransitionState(false).apply { targetState = true }
                }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = scaleIn(
                        initialScale = 0.72f,
                        animationSpec = tween(200, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(200))
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = (rawWidths[index] * factor).coerceAtLeast(10.dp),
                                height = (heightPerMetre * opening.heightM.toFloat() * factor)
                                    .coerceIn(12.dp, rectHeight - 6.dp)
                            )
                            .background(colors.paper, RoundedCornerShape(6.dp))
                            .border(1.dp, colors.outline, RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}

val BOARD_PAD = 8.dp
val BOARD_BORDER = 2.dp
