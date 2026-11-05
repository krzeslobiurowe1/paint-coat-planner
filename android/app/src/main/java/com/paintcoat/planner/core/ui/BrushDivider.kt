package com.paintcoat.planner.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paintcoat.planner.core.theme.LocalAppColors

@Composable
fun BrushDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalAppColors.current.enamel
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
    ) {
        val brush = Brush.horizontalGradient(
            listOf(
                color.copy(alpha = 0f),
                color.copy(alpha = 0.55f),
                color.copy(alpha = 0.15f),
                color.copy(alpha = 0f)
            )
        )
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
        )
    }
}
