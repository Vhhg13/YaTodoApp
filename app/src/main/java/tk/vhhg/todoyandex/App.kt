package tk.vhhg.todoyandex

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import androidx.core.content.edit
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import tk.vhhg.todoyandex.datasource.ITodoRemoteDataSource
import tk.vhhg.todoyandex.datasource.TodoInRamDataSource
import tk.vhhg.todoyandex.datasource.TodoRemoteDataSource
import tk.vhhg.todoyandex.repo.TodoItemsRepository
import tk.vhhg.todoyandex.util.RefreshListWorker
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * The application.
 * Used for DI (for now)
 */
class App : Application() {
    companion object {
        const val BASE_URL = "https://hive.mrdekk.ru/todo/"
        const val SP_DATA = "data"
        const val KEY_LAST_REVISION = "lastRevision"
        const val KEY_DEVICE_ID = "deviceId"
        const val FAIL_PROBABILITY = "20"
        const val REFRESH_LIST_UNIQUE_WORK_NAME = "refresh todo list"
    }

    private val ktor by lazy {
        HttpClient(OkHttp) {
            configureDefaultRequest()
            install(Logging) { level = LogLevel.ALL }
            configureContentNegotiation()
        }
    }

    private val _connectivity = MutableStateFlow(false)
    private val connectivity = _connectivity.asStateFlow()

    //val repo by lazy { TodoItemsFakeRepository() }
    private lateinit var sp: SharedPreferences
    private val remoteDataSource: ITodoRemoteDataSource by lazy { TodoRemoteDataSource(ktor, sp) }
    private val inRamDataSource: TodoInRamDataSource by lazy { TodoInRamDataSource() }


    val repo by lazy {
        TodoItemsRepository(
            remoteDataSource, inRamDataSource,
            sp.getString(KEY_DEVICE_ID, "") ?: "",
            connectivity
        )
    }

    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences(SP_DATA, Context.MODE_PRIVATE)
        if (sp.getString(KEY_DEVICE_ID, "") == "") {
            val newId = UUID.randomUUID().toString()
            sp.edit { putString(KEY_DEVICE_ID, newId) }
        }
        val conn = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        conn.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _connectivity.update { true }
            }

            override fun onLost(network: Network) {
                _connectivity.update { false }
            }
        })
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val refreshListRequest =
            PeriodicWorkRequestBuilder<RefreshListWorker>(8, TimeUnit.HOURS)
                .setConstraints(workerConstraints)
                .build()
        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                REFRESH_LIST_UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                refreshListRequest
            )
    }
}

private fun HttpClientConfig<*>.configureDefaultRequest() {
    defaultRequest {
        url(App.BASE_URL)
        contentType(ContentType.Application.Json)
        header(
            HttpHeaders.Authorization,
            // 🪄🪄🪄 Вы не видели этот токен 🪄🪄🪄
            "OAuth y0_AgAAAABXEp9fAARC0QAAAAEI6rFgAAD4aflCfQlMhJ5ia41Gv2hg0jyyPw"
        )
        header("X-Generate-Fails", App.FAIL_PROBABILITY)
    }
}

private fun HttpClientConfig<*>.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false; encodeDefaults = true
        })
    }
}