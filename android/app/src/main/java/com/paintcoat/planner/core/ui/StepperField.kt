package com.paintcoat.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paintcoat.planner.R
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.NumberLarge

@Composable
fun StepperField(
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    decreaseEnabled: Boolean = true,
    increaseEnabled: Boolean = true
) {
    val colors = LocalAppColors.current
    val decreaseLabel = stringResource(R.string.cd_decrease)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.surfaceAlt, RoundedCornerShape(14.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onDecrease,
            enabled = decreaseEnabled,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = decreaseLabel }
        ) {
            Box(
                modifier = Modifier
                    .size(width = 18.dp, height = 2.dp)
                    .background(
                        if (decreaseEnabled) colors.enamel else colors.textMuted,
                        RoundedCornerShape(1.dp)
                    )
            )
        }
        Text(
            text = value,
            style = NumberLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onIncrease,
            enabled = increaseEnabled,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.cd_increase),
                tint = if (increaseEnabled) colors.enamel else colors.textMuted,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
