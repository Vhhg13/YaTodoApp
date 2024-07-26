package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography


@Composable
fun TodoTextField(body: String, onBodyChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
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
                .defaultMinSize(minHeight = 104.dp)
                .padding(16.dp),
            textStyle = LocalCustomTypography.current.body
                .copy(color = LocalCustomColors.current.labelPrimary),
        )
    }
}