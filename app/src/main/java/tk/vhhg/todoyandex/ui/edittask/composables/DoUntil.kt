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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography
import tk.vhhg.todoyandex.ui.todolist.DateFormatter

@Composable
fun DoUntil(
    onDeadlineSwitchToggle: (Boolean) -> Unit,
    onDeadlineDatePick: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    deadlineDate: Long? = null,
) {
    var isDatePickerOpen by remember { mutableStateOf(false) }
    val deadlineSwitchChecked = deadlineDate != null
    Row(
        modifier = modifier
            .clickable(indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }) {
                isDatePickerOpen = true
            }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val noDeadlineDescription = stringResource(R.string.no_deadline_content_description)
        Column(
            modifier = Modifier.animateContentSize().semantics {
                if(!deadlineSwitchChecked) contentDescription = noDeadlineDescription
            }
        ) {
            Text(
                text = stringResource(id = R.string.do_until),
                style = LocalCustomTypography.current.body,
                color = LocalCustomColors.current.labelPrimary
            )
            if (deadlineSwitchChecked) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = deadlineDate?.let { date ->
                        DateFormatter.sdf.format(date)
                    } ?: "",
                    style = LocalCustomTypography.current.body,
                    color = LocalCustomColors.current.colorBlue
                )
            }
        }
        val switchDescription = stringResource(R.string.deadline_switch_content_description)
        Switch(
            checked = deadlineSwitchChecked,
            onCheckedChange = onDeadlineSwitchToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LocalCustomColors.current.colorBlue,
                checkedTrackColor = LocalCustomColors.current.colorBlue.copy(alpha = 0.3f)
            ),
            modifier = Modifier.semantics {
                contentDescription = switchDescription
            }
        )
    }

    if (isDatePickerOpen) {
        TodoDatePicker(
            onConfirmButtonClick = { date ->
                isDatePickerOpen = false
                onDeadlineDatePick(date)
            },
            onDismiss = { isDatePickerOpen = false },
            initialSelectedDate = deadlineDate
        )
    }
}