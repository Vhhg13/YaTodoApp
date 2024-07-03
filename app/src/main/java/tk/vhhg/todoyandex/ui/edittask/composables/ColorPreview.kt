package tk.vhhg.todoyandex.ui.edittask.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tk.vhhg.todoyandex.ui.theme.AppTheme
import tk.vhhg.todoyandex.ui.theme.LocalCustomColors
import tk.vhhg.todoyandex.ui.theme.LocalCustomTypography

@Preview(
    showBackground = true, backgroundColor = 0xe5e5e5,
    device = "spec:width=1440px,height=460px,dpi=1"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    device = "spec:width=1440px,height=460px,dpi=1",
    backgroundColor = 0xe5e5e5
)
@Composable
fun ColorPreview() {
    AppTheme {
        with(LocalCustomColors.current) {
            val themeName = if (isSystemInDarkTheme()) "Dark" else "Light"
            Column {
                DrawRow(
                    supportSeparator to "Support [$themeName] / Separator",
                    supportOverlay to "Support [$themeName] / Overlay",
                )

                DrawRow(
                    labelPrimary to "Label [$themeName] / Primary",
                    labelSecondary to "Label [$themeName] / Secondary",
                    labelTertiary to "Label [$themeName] / Tertiary",
                    labelDisable to "Label [$themeName] / Disable"
                )

                DrawRow(
                    colorRed to "Color [$themeName] / Red",
                    colorGreen to "Color [$themeName] / Green",
                    colorBlue to "Color [$themeName] / Blue",
                    colorGray to "Color [$themeName] / Gray",
                    colorGrayLight to "Color [$themeName] / Gray Light",
                    colorWhite to "Color [$themeName] / White"
                )

                DrawRow(
                    backPrimary to "Back [$themeName] / Primary",
                    backSecondary to "Back [$themeName] / Secondary",
                    backElevated to "Back [$themeName] / Elevated"
                )
            }
        }
    }
}

private fun Color.toArgbString(): String {
    var argb = toArgb()
    return "#" + buildList {
        repeat(4) {
            if ((it != 3) or (argb.and(0xFF) != 0xFF))
                add("%02X".format(argb and 0xFF))
            argb = argb shr 8
        }
    }.reversed().joinToString("")
}

@Composable
fun DrawRow(vararg colors: Pair<Color, String>) {
    Row(Modifier.padding(10.dp)) {
        for ((color, name) in colors) {
            Box(
                Modifier
                    .background(color)
                    .size(width = 240.dp, height = 100.dp)
            ) {
                Text(
                    text = "$name\n${color.toArgbString()}",
                    style = LocalCustomTypography.current.body,
                    color = getTextColor(color),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 10.dp, start = 12.dp),
                )
            }
        }
    }
}

private fun getTextColor(backgroundColor: Color): Color {
    val argb = backgroundColor.toArgb()
    val red = (argb shr 16 and 0xFF) / 255.0
    val green = (argb shr 8 and 0xFF) / 255.0
    val blue = (argb and 0xFF) / 255.0
    val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue
    return if (luminance > 0.5) Color(0xFF000000) else Color(0xFFFFFFFF)
}