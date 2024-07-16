package tk.vhhg.todoyandex.ui.infoscreen

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.di.Constants

class TodoDivActionHandler(
    private val goBack: () -> Unit
) : DivActionHandler() {

    override fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        resolver: ExpressionResolver
    ): Boolean {
        val url = action.url?.evaluate(resolver) ?: return super.handleAction(action, view, resolver)

        return (url.scheme == SCHEME_SAMPLE && handleSampleAction(url, view.view.context)) ||
            super.handleAction(action, view, resolver)
    }

    private fun handleSampleAction(action: Uri, context: Context): Boolean {
        return when (action.host) {
            "goback" -> {
                goBack()
                true
            }
            "theme" -> changeAppTheme(action.query, context)
            else -> false
        }
    }

    private fun changeAppTheme(query: String?, context: Context): Boolean{
        val nightMode = when(query){
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        (context.applicationContext as App).appComponent.getPreferences().edit {
            putInt(Constants.THEME_PREFERENCE, nightMode)
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        return true
    }

    companion object {
        const val SCHEME_SAMPLE = "todo-action"
    }
}
