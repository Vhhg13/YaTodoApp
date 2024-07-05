package tk.vhhg.todoyandex.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import tk.vhhg.todoyandex.App

/**
 * Fetches data from the network in the background
 */
class RefreshListWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        (applicationContext as App).repo.refresh()
        return Result.success()
    }
}