package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography

@Composable
fun Priority(
    priority: TodoItemPriority,
    onPriorityChanged: (TodoItemPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    var priorityDropdownExpanded by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier
        .clickable { priorityDropdownExpanded = true }
        .padding(vertical = 16.dp)) {
        Text(
            text = stringResource(id = R.string.priority),
            style = LocalCustomTypography.current.body,
            color = LocalCustomColors.current.labelPrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = getPriorityString(priority),
            style = LocalCustomTypography.current.subhead,
            color = LocalCustomColors.current.labelTertiary
        )
        DropdownMenu(
            modifier = Modifier.background(LocalCustomColors.current.backElevated),
            expanded = priorityDropdownExpanded,
            onDismissRequest = { priorityDropdownExpanded = false }) {
            DropdownMenuItem(
                onClick = {
                    priorityDropdownExpanded = false
                    onPriorityChanged(TodoItemPriority.MEDIUM)
                },
                text = {
                    Text(
                        text = getPriorityString(priority = TodoItemPriority.MEDIUM),
                        color = LocalCustomColors.current.labelPrimary,
                        style = LocalCustomTypography.current.body
                    )
                })
            DropdownMenuItem(
                onClick = {
                    priorityDropdownExpanded = false
                    onPriorityChanged(TodoItemPriority.LOW)
                },
                text = {
                    Text(
                        text = getPriorityString(priority = TodoItemPriority.LOW),
                        color = LocalCustomColors.current.labelPrimary,
                        style = LocalCustomTypography.current.body
                    )
                }
            )
            DropdownMenuItem(
                onClick = {
                    priorityDropdownExpanded = false
                    onPriorityChanged(TodoItemPriority.HIGH)
                },
                text = {
                    Text(
                        text = getPriorityString(priority = TodoItemPriority.HIGH),
                        color = LocalCustomColors.current.colorRed,
                        style = LocalCustomTypography.current.body
                    )
                }
            )
        }
    }

}

@Composable
fun getPriorityString(priority: TodoItemPriority): String {
    return when (priority) {
        TodoItemPriority.HIGH -> stringResource(id = R.string.high_priority_menu_item)
        TodoItemPriority.MEDIUM -> stringResource(id = R.string.medium_priority_menu_item)
        TodoItemPriority.LOW -> stringResource(id = R.string.low_priority_menu_item)
    }
}