package tk.vhhg.todoyandex

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.yandex.div.picasso.PicassoDivImageLoader
import tk.vhhg.todoyandex.di.ApplicationComponent
import tk.vhhg.todoyandex.di.Constants
import tk.vhhg.todoyandex.di.DaggerApplicationComponent
import tk.vhhg.todoyandex.util.RefreshListWorker

class App : Application(), Configuration.Provider {
    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        RefreshListWorker.initialize(this)
        getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .getInt(Constants.THEME_PREFERENCE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            .also { AppCompatDelegate.setDefaultNightMode(it) }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(
                appComponent.getWorkerFactory()
            ).build()
}