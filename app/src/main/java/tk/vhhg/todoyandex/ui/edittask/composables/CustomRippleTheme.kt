package tk.vhhg.todoyandex.ui.edittask.composables

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

class CustomRippleTheme(private val color: Color): RippleTheme {
    @Composable
    @ReadOnlyComposable
    override fun defaultColor() = color

    @Composable
    @ReadOnlyComposable
    override fun rippleAlpha() = RippleAlpha(
        draggedAlpha = 0.2f,
        focusedAlpha = 0.2f,
        hoveredAlpha = 0.2f,
        pressedAlpha = 0.2f,
    )
}