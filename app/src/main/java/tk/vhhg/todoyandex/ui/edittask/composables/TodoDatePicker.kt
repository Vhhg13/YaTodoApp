package tk.vhhg.todoyandex.ui.edittask.composables

import android.content.res.Configuration
import android.os.SystemClock
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.ui.theme.AppTheme
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDatePicker(modifier: Modifier = Modifier, onConfirmButtonClick: (Long?) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates{
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Date().time.let{ it-it.mod(86400000) }
            }
        }
    )
    DatePickerDialog(
        colors = DatePickerDefaults.colors().copy(
            containerColor = LocalCustomColors.current.backSecondary
        ),
        shape = RoundedCornerShape(2.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onConfirmButtonClick(datePickerState.selectedDateMillis)
                }
            ) {
                Text(text = stringResource(R.string.select_date), color = LocalCustomColors.current.colorBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dismiss), color = LocalCustomColors.current.colorBlue)
            }
        }
    ) {
        DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(
            selectedDayContainerColor = LocalCustomColors.current.colorBlue,
            dayContentColor = LocalCustomColors.current.labelPrimary,
            selectedDayContentColor = LocalCustomColors.current.colorWhite,
            currentYearContentColor = LocalCustomColors.current.colorBlue,
            navigationContentColor = LocalCustomColors.current.labelSecondary,
            todayContentColor = LocalCustomColors.current.colorBlue,
            todayDateBorderColor = LocalCustomColors.current.backSecondary,
            weekdayContentColor = LocalCustomColors.current.labelTertiary,
            headlineContentColor = LocalCustomColors.current.labelPrimary,
            titleContentColor = LocalCustomColors.current.labelPrimary,
            disabledDayContentColor = LocalCustomColors.current.labelTertiary
        ))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview
@Composable
fun DatePickerPreview(){
    AppTheme {
        TodoDatePicker(
            Modifier,{}, {}
        )
    }
}