package tk.vhhg.todoyandex.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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