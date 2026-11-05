package com.paintcoat.planner.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paintcoat.planner.R
import com.paintcoat.planner.core.theme.LocalAppColors
import com.paintcoat.planner.core.theme.NumberMedium
import com.paintcoat.planner.core.ui.SectionHeader
import com.paintcoat.planner.core.ui.StepperField

@Composable
fun AddWallDialog(
    widthText: String,
    heightText: String,
    areaText: String,
    onWidthDecrease: () -> Unit,
    onWidthIncrease: () -> Unit,
    onHeightDecrease: () -> Unit,
    onHeightIncrease: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = colors.surfaceCard,
        title = {
            Text(
                text = stringResource(R.string.dialog_add_wall_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(title = stringResource(R.string.label_wall_width))
                StepperField(
                    value = widthText,
                    onDecrease = onWidthDecrease,
                    onIncrease = onWidthIncrease
                )
                SectionHeader(title = stringResource(R.string.label_wall_height))
                StepperField(
                    value = heightText,
                    onDecrease = onHeightDecrease,
                    onIncrease = onHeightIncrease
                )
                Text(
                    text = areaText,
                    style = NumberMedium,
                    color = colors.amberDeep
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.cta_add),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.enamel
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_cancel),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textSecondary
                )
            }
        }
    )
}
