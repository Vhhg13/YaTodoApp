package tk.vhhg.todoyandex.ui.infoscreen

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.LocalDensity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.picasso.PicassoDivImageLoader
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.di.Constants
import tk.vhhg.todoyandex.model.ListItemType
import javax.inject.Inject

class InfoFragment : Fragment() {
    private val assetReader by lazy { AssetReader(requireContext()) }
    private val navController by lazy { findNavController() }

    private lateinit var preferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferences = (context.applicationContext as App).appComponent.getPreferences()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val divJson = assetReader.read("divkitScreen.json")
        val templatesJson = divJson.optJSONObject("templates")
        val cardJson = divJson.getJSONObject("card")
        when(preferences.getInt(Constants.THEME_PREFERENCE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)){
            AppCompatDelegate.MODE_NIGHT_YES -> cardJson.getJSONArray("variables").getJSONObject(0).put("value", "dark")
            AppCompatDelegate.MODE_NIGHT_NO -> cardJson.getJSONArray("variables").getJSONObject(0).put("value", "light")
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> cardJson.getJSONArray("variables").getJSONObject(0).put("value", "system")
        }

        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK){
            Configuration.UI_MODE_NIGHT_NO -> cardJson.getJSONArray("variables").getJSONObject(1).put("value", "light")
            Configuration.UI_MODE_NIGHT_YES -> cardJson.getJSONArray("variables").getJSONObject(1).put("value", "dark")
        }

        val itemTypePreferenceString = preferences.getString(Constants.ITEM_TYPE_PREFERENCE, ListItemType.NONCUSTOM.name)
        cardJson.getJSONArray("variables").getJSONObject(2).put("value", itemTypePreferenceString)

        val divContext = Div2Context(
            baseContext = requireActivity(),
            configuration = createDivConfiguration(),
            lifecycleOwner = viewLifecycleOwner
        )
        Log.d("divhandler", "onCreateView ${preferences.getInt(Constants.THEME_PREFERENCE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)} ${resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK}")
        val divView = Div2ViewFactory(divContext, templatesJson).createView(cardJson)
        return ScrollView(requireContext()).apply {
            addView(divView)
        }
    }
    private fun createDivConfiguration(): DivConfiguration {
        return DivConfiguration.Builder(PicassoDivImageLoader(requireContext()))
            .actionHandler(TodoDivActionHandler(navController::popBackStack))
            .visualErrorsEnabled(true)
            .build()
    }
}