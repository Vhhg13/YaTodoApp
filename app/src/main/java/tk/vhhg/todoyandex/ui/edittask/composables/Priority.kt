package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Priority(
    priority: TodoItemPriority,
    onPriorityChanged: (TodoItemPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var animatePriorityBackground by remember { mutableStateOf(false) }
    val lightRed = LocalCustomColors.current.colorRed.copy(alpha = 0.5f)
    val animationDuration = 200
    val backgroundColor by animateColorAsState(
        targetValue = if (animatePriorityBackground && priority == TodoItemPriority.HIGH) lightRed else LocalCustomColors.current.backPrimary,
        animationSpec = tween(durationMillis = animationDuration),
        label = "highPriorityIndication"
    )

    Column(modifier = modifier
        .clickable(indication = rememberRipple(),
            interactionSource = remember { MutableInteractionSource() }) {
            showBottomSheet = true
        }
        .background(backgroundColor)
        .padding(vertical = 16.dp)) {
        Text(
            text = stringResource(id = R.string.priority),
            style = LocalCustomTypography.current.body,
            color = LocalCustomColors.current.labelPrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(text = getPriorityString(priority),
            style = LocalCustomTypography.current.subhead,
            color = with(LocalCustomColors.current) {
                if (priority == TodoItemPriority.HIGH) colorRed else labelTertiary
            })
    }

    if (showBottomSheet) {
        PriorityBottomSheet(
            sheetState = sheetState,
            chosenPriority = priority,
            onPriorityChanged = {
                onPriorityChanged(it)
                scope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                    animatePriorityBackground = true
                    delay(animationDuration * 2L)
                    animatePriorityBackground = false
                }
            },
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
        )
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