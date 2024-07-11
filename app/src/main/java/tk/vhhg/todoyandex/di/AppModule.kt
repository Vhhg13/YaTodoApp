package tk.vhhg.todoyandex.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import androidx.core.content.edit
import androidx.room.Room
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import tk.vhhg.todoyandex.datasource.local.db.AppDatabase
import tk.vhhg.todoyandex.datasource.local.ITodoLocalDataSource
import tk.vhhg.todoyandex.datasource.local.db.TodoItemDao
import tk.vhhg.todoyandex.datasource.local.db.TodoLocalDataSource
import tk.vhhg.todoyandex.datasource.local.preferences.IRevisionLocalDataSource
import tk.vhhg.todoyandex.datasource.local.preferences.RevisionLocalDataSource
import tk.vhhg.todoyandex.datasource.remote.ITodoRemoteDataSource
import tk.vhhg.todoyandex.datasource.remote.TodoRemoteDataSource
import tk.vhhg.todoyandex.repo.ITodoItemsRepository
import tk.vhhg.todoyandex.repo.TodoItemsRepository
import tk.vhhg.todoyandex.util.RefreshListWorker
import java.util.UUID


@Module
interface AppModule {
    companion object {
        @Provides
        @TodoAppScope
        fun provideHttpClient(): HttpClient {
            return HttpClient(OkHttp) {
                configureDefaultRequest()
                install(Logging) { level = LogLevel.ALL }
                configureContentNegotiation()
                configureRetries()
            }
        }

        @Provides
        @TodoAppScope
        @LastRevisionPreferences
        fun provideSharedPreferences(ctx: Context): SharedPreferences {
            return ctx.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        }

        @Provides
        @TodoAppScope
        @DeviceId
        fun provideDeviceId(@LastRevisionPreferences sp: SharedPreferences): String {
            if (sp.getString(Constants.SP_KEY_DEVICE_ID, "") == "") {
                val newId = UUID.randomUUID().toString()
                sp.edit { putString(Constants.SP_KEY_DEVICE_ID, newId) }
                return newId
            }
            return sp.getString(Constants.SP_KEY_DEVICE_ID, "") ?: ""
        }

        @Provides
        @TodoAppScope
        @Connectivity
        fun provideConnectivity(ctx: Context): StateFlow<Boolean> {
            val connectivity = MutableStateFlow(false)
            val conn = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            conn.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    connectivity.update { true }
                }

                override fun onLost(network: Network) {
                    connectivity.update { false }
                }
            })
            return connectivity.asStateFlow()
        }

        @Provides
        @TodoAppScope
        fun provideWorkerFactory(repo: ITodoItemsRepository): WorkerFactory {
            return object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    if (workerClassName == RefreshListWorker::class.java.name)
                        return RefreshListWorker(appContext, workerParameters, repo)
                    return null
                }
            }
        }

        @Provides
        @TodoAppScope
        fun provideDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "TodoItemsDatabase"
            ).build()
        }

        @Provides
        @TodoAppScope
        fun provideTodoItemsDao(db: AppDatabase): TodoItemDao {
            return db.todoDao()
        }
    }


    @Binds
    @TodoAppScope
    fun bindTodoRemoteDataSource(ds: TodoRemoteDataSource): ITodoRemoteDataSource

    @Binds
    @TodoAppScope
    fun bindTodoItemsRepository(repo: TodoItemsRepository): ITodoItemsRepository

    @Binds
    @TodoAppScope
    fun bindTodoLocalDataSource(ds: TodoLocalDataSource): ITodoLocalDataSource

    @Binds
    @TodoAppScope
    fun bindRevisionLocalDataSource(ds: RevisionLocalDataSource): IRevisionLocalDataSource
}

private fun HttpClientConfig<*>.configureDefaultRequest() {
    defaultRequest {
        url(Constants.BASE_URL)
        contentType(ContentType.Application.Json)
        header(
            HttpHeaders.Authorization,
            // 🪄🪄🪄 Вы не видели этот токен 🪄🪄🪄
            "OAuth y0_AgAAAABXEp9fAARC0QAAAAEI6rFgAAD4aflCfQlMhJ5ia41Gv2hg0jyyPw"
        )
        header("X-Generate-Fails", Constants.NETWORK_FAIL_PROBABILITY)
    }
}

private fun HttpClientConfig<*>.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
            encodeDefaults = true
            ignoreUnknownKeys = true // TODO: Добавить поддержку нового поля `files`
        })
    }
}

private fun HttpClientConfig<*>.configureRetries() {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 5)
        exponentialDelay(base = 1.0, maxDelayMs = 5000)
    }
}