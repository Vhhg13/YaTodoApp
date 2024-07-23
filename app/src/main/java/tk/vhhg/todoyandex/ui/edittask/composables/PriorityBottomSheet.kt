package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PriorityBottomSheet(
    sheetState: SheetState,
    chosenPriority: TodoItemPriority,
    onPriorityChanged: (TodoItemPriority) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = LocalCustomColors.current.backPrimary,
        modifier = modifier
    ) {
        listOf(
            LocalCustomColors.current.labelPrimary to TodoItemPriority.LOW,
            LocalCustomColors.current.labelPrimary to TodoItemPriority.MEDIUM,
            LocalCustomColors.current.colorRed to TodoItemPriority.HIGH
        ).forEach { (textColor, priority) ->
            BottomSheetButton(
                priority = priority, textColor = textColor, onClick = {
                    onPriorityChanged(priority)
                }, isChosen = priority == chosenPriority
            )
        }
        Spacer(
            Modifier.height(
                WindowInsets.navigationBarsIgnoringVisibility.asPaddingValues()
                    .calculateBottomPadding()
            )
        )
    }
}

@Composable
fun BottomSheetButton(
    priority: TodoItemPriority,
    textColor: Color,
    onClick: () -> Unit,
    isChosen: Boolean,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RectangleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = getPriorityString(priority),
                color = textColor,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = 16.dp),
                textAlign = TextAlign.Start
            )
            if (isChosen) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Chosen priority",
                    modifier = Modifier.padding(end = 16.dp),
                    tint = LocalCustomColors.current.labelPrimary
                )
            }
        }
    }
}