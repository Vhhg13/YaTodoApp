package tk.vhhg.todoyandex.ui.edittask.composables

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.ui.theme.AppTheme
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography
import tk.vhhg.todoyandex.ui.todolist.DateFormatter
import java.util.Date

@Composable
fun EditTaskScreen(
    body: String,
    priority: TodoItemPriority,
    deadline: Date?,
    deleteButtonEnabled: Boolean,
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
            Card(
                modifier = Modifier
                    .defaultMinSize(minHeight = 104.dp)
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(LocalCustomColors.current.backSecondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                BasicTextField(
                    minLines = 3,
                    decorationBox = {  innerTextField ->
                        if (body.isEmpty()) {
                            Text(
                                stringResource(R.string.todo_item_text_hint),
                                color = LocalCustomColors.current.labelTertiary,
                                style = LocalCustomTypography.current.body
                            )
                        }
                        innerTextField()
                    },
                    value = body,
                    onValueChange = onBodyChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textStyle = LocalCustomTypography.current.body
                        .copy(color = LocalCustomColors.current.labelPrimary),
                )
            }
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

            var isDatePickerOpen by remember { mutableStateOf(false) }
            if (isDatePickerOpen) {
                TodoDatePicker(
                    onConfirmButtonClick = { date ->
                        isDatePickerOpen = false
                        onDeadlineDatePick(date)
                    },
                    onDismiss = { isDatePickerOpen = false }
                )
            }

            DoUntil(
                deadlineSwitchChecked = deadline != null,
                onDeadlineSwitchToggle = onDeadlineSwitchToggle,
                onClick = {
                    isDatePickerOpen = true
                },
                deadlineDate = deadline?.let { date ->
                    DateFormatter.sdf.format(date)
                } ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.size(24.dp))

            HorizontalDivider(color = LocalCustomColors.current.supportSeparator)

            CompositionLocalProvider(LocalRippleTheme provides CustomRippleTheme(LocalCustomColors.current.colorRed)) {
                TextButton(
                    onClick = onDeleteButtonClick,
                    enabled = deleteButtonEnabled,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = LocalCustomColors.current.colorRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete current todo",
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = stringResource(id = R.string.remove),
                            style = LocalCustomTypography.current.body // Здесь, как и во всех @Composable в пакете `tk.vhhg.todoyandex.ui.edittask.composables`, стиль текста берётся из кастомной типографии
                        )
                    }
                }
            }
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
            deleteButtonEnabled = true,
            onBodyChange = {},
            onPriorityChange = {},
            onDeadlineSwitchToggle = {},
            onDeadlineDatePick = {},
            onTopBarCloseClick = {},
            onTopBarSaveClick = {},
            onDeleteButtonClick = {})
    }
}