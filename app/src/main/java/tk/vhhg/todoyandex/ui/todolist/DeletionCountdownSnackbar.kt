package tk.vhhg.todoyandex.ui.todolist

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.core.view.ViewCompat
import androidx.core.view.allViews
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.model.TodoItem


class DeletionCountdownSnackbar(
    view: View,
    private val countdownDuration: Int,
    private val deletedItem: TodoItem,
    private val restore: (TodoItem) -> Unit,
    private val onCloseSnackbar: () -> Unit
) {
    private val context = view.context

    private val isTalkBackActive = (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled

    private val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE).apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        setTextMaxLines(1)
        setTextColor(
            context.resources.getInteger(R.integer.snackbar_cd_start_color).xor(0xFFFFFF)
        )
        getView().allViews.forEach { it.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO }
        getView().importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        getView().accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_NONE
        getView().contentDescription = context.getString(R.string.deletion_announcement)
        val btn = getView().allViews.find { it is MaterialButton }
        ViewCompat.addAccessibilityAction(getView(), context.getString(R.string.cancel)) { _, _ ->
            btn?.performClick()
            true
        }
    }

    fun showOn(coroutineScope: CoroutineScope){
        coroutineScope.launch {
            snackbar.setAction(R.string.restore_deleted) {
                restore(deletedItem)
                snackbar.dismiss()
                onCloseSnackbar()
                cancel()
            }.show()

            launch {
                delay(2000)
                snackbar.view.announceForAccessibility(context.getString(R.string.deletion_announcement))
            }

            animateBackgroundTint()

            count(countdownDuration)

            if(isTalkBackActive) return@launch

            for (countdown in countdownDuration-1 downTo 0) {
                delay(1000)
                count(countdown)
            }
            snackbar.dismiss()
            onCloseSnackbar()
        }
    }

    private fun animateBackgroundTint(){
        ValueAnimator.ofArgb(
            context.resources.getInteger(R.integer.snackbar_cd_start_color),
            context.resources.getInteger(R.integer.snackbar_cd_end_color)
        ).apply {
            duration = countdownDuration * 1000L
            addUpdateListener {
                snackbar.setBackgroundTint(it.animatedValue as Int)
            }
            start()
        }
    }

    private fun count(countdown: Int){
        val timeLeftText = context.getString(R.string.time_left_countdown, countdown)
        snackbar.setText(context.getString(
            R.string.deletion_countdown,
            if(isTalkBackActive) "" else "$timeLeftText ",
            deletedItem.body,
        ))
    }
}