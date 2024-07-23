package tk.vhhg.todoyandex.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CustomTypography(
    val largeTitle: TextStyle,
    val title: TextStyle,
    val button: TextStyle,
    val body: TextStyle,
    val subhead: TextStyle,
)

val DefaultTypography = CustomTypography(
    largeTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight(500)
    ),
    title = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight(500)
    ),
    button = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(500)
    ),
    body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight(400)
    ),
    subhead = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight(400)
    )
)

val LocalCustomTypography = staticCompositionLocalOf {
    DefaultTypography
}

@Preview
@Composable
fun TypographyPreview() {
    AppTheme {
        Column(Modifier.background(Color.White).padding(32.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(text = "Large title - 32/38", style = LocalCustomTypography.current.largeTitle)
            Text(text = "Title - 20/32", style = LocalCustomTypography.current.title)
            Text(text = "BUTTON - 14/24", style = LocalCustomTypography.current.button)
            Text(text = "Body - 16/20", style = LocalCustomTypography.current.body)
            Text(text = "Subhead - 14/20", style = LocalCustomTypography.current.subhead)
        }
    }
}