package tk.vhhg.todoyandex.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.di.Constants
import tk.vhhg.todoyandex.repo.ITodoItemsRepository
import java.util.concurrent.TimeUnit

/**
 * Fetches data from the network in the background
 */
class RefreshListWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repo: ITodoItemsRepository
) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        repo.sync()
        return Result.success()
    }

    companion object {
        private const val REPEAT_INTERVAL = 8L
        fun initialize(app: App) {
            val workerConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val refreshListRequest =
                PeriodicWorkRequestBuilder<RefreshListWorker>(REPEAT_INTERVAL, TimeUnit.HOURS)
                    .setInitialDelay(REPEAT_INTERVAL, TimeUnit.HOURS)
                    .setConstraints(workerConstraints)
                    .build()
            WorkManager
                .getInstance(app)
                .enqueueUniquePeriodicWork(
                    Constants.REFRESH_LIST_UNIQUE_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    refreshListRequest
                )
        }
    }
}