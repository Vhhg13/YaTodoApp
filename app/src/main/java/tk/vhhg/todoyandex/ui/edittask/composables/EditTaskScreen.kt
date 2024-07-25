package tk.vhhg.todoyandex.ui.edittask.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.ui.theme.AppTheme
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import java.util.Date

@Composable
fun EditTaskScreen(
    body: String,
    priority: TodoItemPriority,
    deadline: Date?,
    isDeleteButtonEnabled: Boolean,
    onBodyChange: (String) -> Unit,
    onPriorityChange: (TodoItemPriority) -> Unit,
    onDeadlineSwitchToggle: (Boolean) -> Unit,
    onDeadlineDatePick: (Long?) -> Unit,
    onTopBarCloseClick: () -> Unit,
    onTopBarSaveClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            Surface(
                color = LocalCustomColors.current.backPrimary,
                shadowElevation = scrollState.value.div(16).coerceAtMost(8).dp
            ) {
                EditTaskTopBar(onTopBarCloseClick, onTopBarSaveClick)
            }
        },
        containerColor = LocalCustomColors.current.backPrimary
    )
    { paddingValues ->
        Spacer(Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            TodoTextField(
                body = body,
                onBodyChange = onBodyChange,
                modifier = Modifier
                    .defaultMinSize(minHeight = 104.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))
            Priority(
                priority = priority,
                onPriorityChanged = onPriorityChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            HorizontalDivider(
                color = LocalCustomColors.current.supportSeparator,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            DoUntil(
                onDeadlineSwitchToggle = onDeadlineSwitchToggle,
                deadlineDate = deadline?.time,
                onDeadlineDatePick = onDeadlineDatePick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.size(24.dp))

            HorizontalDivider(color = LocalCustomColors.current.supportSeparator)

            DeleteButton(
                onDeleteButtonClick = onDeleteButtonClick,
                isDeleteButtonEnabled = isDeleteButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview
@Composable
fun EditTaskScreenPreview() {
    AppTheme {
        EditTaskScreen(
            body = "Что не надо сделать,,,",
            priority = TodoItemPriority.HIGH,
            deadline = Date(),
            isDeleteButtonEnabled = true,
            onBodyChange = {},
            onPriorityChange = {},
            onDeadlineSwitchToggle = {},
            onDeadlineDatePick = {},
            onTopBarCloseClick = {},
            onTopBarSaveClick = {},
            onDeleteButtonClick = {})
    }
}