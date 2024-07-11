package tk.vhhg.todoyandex

import android.app.Application
import androidx.work.Configuration
import tk.vhhg.todoyandex.di.ApplicationComponent
import tk.vhhg.todoyandex.di.DaggerApplicationComponent
import tk.vhhg.todoyandex.util.RefreshListWorker

class App : Application(), Configuration.Provider {
    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        //RefreshListWorker.initialize(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(
                appComponent.getWorkerFactory()
            ).build()
}