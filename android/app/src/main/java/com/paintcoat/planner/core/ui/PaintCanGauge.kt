package com.paintcoat.planner.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.paintcoat.planner.R
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.MeniscusAmber

@Composable
fun PaintCanGauge(
    fillFraction: Float,
    description: String,
    onFilled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(fillFraction) {
        progress.animateTo(
            targetValue = fillFraction.coerceIn(0f, 1f),
            animationSpec = tween(350, easing = FastOutSlowInEasing)
        )
        onFilled()
    }
    Box(
        modifier = modifier
            .width(160.dp)
            .height(190.dp)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp,
                        bottomStart = 14.dp,
                        bottomEnd = 14.dp
                    )
                )
        ) {
            val radius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
            drawRoundRect(color = colors.surfaceAlt, cornerRadius = radius)
            val fillHeight = size.height * progress.value
            if (fillHeight > 0f) {
                drawRect(
                    color = colors.amber,
                    topLeft = Offset(0f, size.height - fillHeight),
                    size = Size(size.width, fillHeight)
                )
                drawRect(
                    color = MeniscusAmber,
                    topLeft = Offset(0f, size.height - fillHeight),
                    size = Size(size.width, 6.dp.toPx())
                )
            }
            drawRoundRect(
                color = colors.enamel,
                cornerRadius = radius,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Fit
        )
    }
}
