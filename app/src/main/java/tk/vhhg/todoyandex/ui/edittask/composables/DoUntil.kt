package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography

@Composable
fun DoUntil(
    deadlineSwitchChecked: Boolean,
    onDeadlineSwitchToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    deadlineDate: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Text(
                text = stringResource(id = R.string.do_until),
                style = LocalCustomTypography.current.body,
                color = LocalCustomColors.current.labelPrimary
            )
            if (deadlineSwitchChecked) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = deadlineDate,
                    style = LocalCustomTypography.current.body,
                    color = LocalCustomColors.current.colorBlue
                )
            }
        }

        Switch(
            checked = deadlineSwitchChecked,
            onCheckedChange = onDeadlineSwitchToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LocalCustomColors.current.colorBlue,
                checkedTrackColor = LocalCustomColors.current.colorBlue.copy(alpha = 0.3f)
            )
        )
    }
}