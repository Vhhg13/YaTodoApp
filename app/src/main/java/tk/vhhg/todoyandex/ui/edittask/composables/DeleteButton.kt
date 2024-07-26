package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography


@Composable
fun DeleteButton(onDeleteButtonClick: () -> Unit, isDeleteButtonEnabled: Boolean, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalRippleTheme provides CustomRippleTheme(LocalCustomColors.current.colorRed)) {
        val context = LocalContext.current
        TextButton(
            onClick = onDeleteButtonClick,
            enabled = isDeleteButtonEnabled,
            colors = ButtonDefaults.textButtonColors(
                contentColor = LocalCustomColors.current.colorRed
            ),
            modifier = modifier.clearAndSetSemantics {
                contentDescription =
                    context.getString(R.string.remove_todo_content_description)
                role = Role.Button
                if(!isDeleteButtonEnabled) stateDescription = context.getString(R.string.button_disabled)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = stringResource(id = R.string.remove),
                    style = LocalCustomTypography.current.body
                )
            }
        }
    }
}
