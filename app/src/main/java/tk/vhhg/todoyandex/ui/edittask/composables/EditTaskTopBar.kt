package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography

@Composable
fun EditTaskTopBar(onCloseClick: () -> Unit, onSaveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close the item editing screen",
                tint = LocalCustomColors.current.labelPrimary
            )
        }
        TextButton(onClick = onSaveClick) {
            Text(
                text = stringResource(id = R.string.save).uppercase(),
                color = LocalCustomColors.current.colorBlue,
                style = LocalCustomTypography.current.button
            )
        }
    }
}