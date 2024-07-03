package tk.vhhg.todoyandex.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val supportSeparator: Color,
    val supportOverlay: Color,
    val labelPrimary: Color,
    val labelSecondary: Color,
    val labelTertiary: Color,
    val labelDisable: Color,
    val colorRed: Color,
    val colorGreen: Color,
    val colorBlue: Color,
    val colorGray: Color,
    val colorGrayLight: Color,
    val colorWhite: Color,
    val backPrimary: Color,
    val backSecondary: Color,
    val backElevated: Color
)


val LightColorPalette = CustomColors(
    supportSeparator = support_light_separator,
    supportOverlay = support_light_overlay,
    labelPrimary = label_light_primary,
    labelSecondary = label_light_secondary,
    labelTertiary = label_light_tertiary,
    labelDisable = label_light_disable,
    colorRed = color_light_red,
    colorGreen = color_light_green,
    colorBlue = color_light_blue,
    colorGray = color_light_gray,
    colorGrayLight = color_light_gray_light,
    colorWhite = color_light_white,
    backPrimary = back_light_primary,
    backSecondary = back_light_secondary,
    backElevated = back_light_elevated
)


val DarkColorPalette = CustomColors(
    supportSeparator = support_dark_separator,
    supportOverlay = support_dark_overlay,
    labelPrimary = label_dark_primary,
    labelSecondary = label_dark_secondary,
    labelTertiary = label_dark_tertiary,
    labelDisable = label_dark_disable,
    colorRed = color_dark_red,
    colorGreen = color_dark_green,
    colorBlue = color_dark_blue,
    colorGray = color_dark_gray,
    colorGrayLight = color_dark_gray_light,
    colorWhite = color_dark_white,
    backPrimary = back_dark_primary,
    backSecondary = back_dark_secondary,
    backElevated = back_dark_elevated
)

val LocalCustomColors = staticCompositionLocalOf {
    LightColorPalette // Default to light theme
}