package com.paintcoat.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paintcoat.planner.core.theme.LocalAppColors

@Composable
fun SwatchCard(
    modifier: Modifier = Modifier,
    accent: Color? = null,
    container: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(16.dp)
    val base = Modifier
        .shadow(
            elevation = 4.dp,
            shape = shape,
            ambientColor = colors.enamel.copy(alpha = 0.10f),
            spotColor = colors.enamel.copy(alpha = 0.10f)
        )
        .clip(shape)
        .background(container ?: colors.surfaceCard)
    val clickModifier = if (onClick != null) base.clickable(onClick = onClick) else base
    Column(modifier = modifier.then(clickModifier)) {
        if (accent != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(accent)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            content = content
        )
    }
}
