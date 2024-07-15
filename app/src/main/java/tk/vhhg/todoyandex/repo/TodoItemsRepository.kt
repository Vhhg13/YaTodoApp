package tk.vhhg.todoyandex.repo

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.datasource.local.ITodoLocalDataSource
import tk.vhhg.todoyandex.datasource.local.preferences.IRevisionLocalDataSource
import tk.vhhg.todoyandex.datasource.remote.ITodoRemoteDataSource
import tk.vhhg.todoyandex.di.Connectivity
import tk.vhhg.todoyandex.di.DeviceId
import tk.vhhg.todoyandex.di.TodoAppScope
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import javax.inject.Inject

/**
 * An implementation of [ITodoItemsRepository]
 */
@TodoAppScope
class TodoItemsRepository @Inject constructor(
    private val remoteDataSource: ITodoRemoteDataSource,
    private val localDataSource: ITodoLocalDataSource,
    @DeviceId private val deviceId: String,
    @Connectivity private val connectivity: StateFlow<Boolean>,
    private val revision: IRevisionLocalDataSource,
) : ITodoItemsRepository {

    private val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("repo handler", throwable.message ?: "")
        repositoryScope.launch {
            // `runCatching` ensures the handler does not throw exceptions into itself
            runCatching { _errors.emit(Result.UnknownError(throwable)) }
        }
    }
    private val repositoryScope: CoroutineScope = CoroutineScope(
        Dispatchers.IO + SupervisorJob() + handler
    )

    init {
        repositoryScope.launch {
            connectivity.collect { hasNetworkConnection ->
                if (hasNetworkConnection) sync()
            }
        }
    }

    override fun add(item: TodoItem) {
        repositoryScope.launch {
            localDataSource.add(item.updatedBy(deviceId))
            debounceAndPatchList()
        }
    }

    override fun toggle(item: TodoItem) {
        repositoryScope.launch {
            localDataSource.toggle(item.updatedBy(deviceId))
            debounceAndPatchList()
        }
    }

    override fun generateId() = localDataSource.generateId()

    override fun update(item: TodoItem) {
        repositoryScope.launch {
            localDataSource.update(item.updatedBy(deviceId))
            debounceAndPatchList()
        }
    }

    override fun remove(item: TodoItem?) {
        if (item == null) return
        repositoryScope.launch {
            localDataSource.remove(item)
            debounceAndPatchList()
        }
    }

    private suspend fun <T> Result<T>.emitErrorsInto(flow: MutableSharedFlow<Result<Unit>>){
        when(this){
            Result.NoInternetConnection -> flow.emit(Result.NoInternetConnection)
            Result.NotFound -> flow.emit(Result.NotFound)
            is Result.RevisionsDoNotMatch -> flow.emit(Result.RevisionsDoNotMatch(this.revision))
            Result.ServerError -> flow.emit(Result.ServerError)
            is Result.Success -> flow.emit(Result.Success(Unit))
            Result.Unauthorized -> flow.emit(Result.Unauthorized)
            is Result.UnknownError -> flow.emit(Result.UnknownError(this.err))
        }
    }

    override fun sync() {
        repositoryScope.launch {
            cancelDebounce()
            val result = remoteDataSource.getList()
            result.emitErrorsInto(_errors)
            (result as? Result.Success)?.let {
                Log.d("trace list (result as success)", result.value.list.toString())
                val remoteRevision = checkNotNull(result.value.revision)
                val localRevision = revision.get()
                if(localRevision < remoteRevision){
                    localDataSource.merge(result.value.list)
                }
                val patchResult = remoteDataSource.patchList(localDataSource.getCurrentList())
                patchResult.emitErrorsInto(_errors)
                patchResult as? Result.Success
            }?.let { patchResult ->
                revision.set(patchResult.value.revision)
                Log.d("trace list (patchResult as success)", patchResult.value.list.toString())
                localDataSource.refresh(patchResult.value.list)
            }
        }
    }

    override suspend fun findById(id: String?): TodoItem? =
        localDataSource.findById(id)

    override val items: Flow<List<TodoItem>> = localDataSource.items

    private val _errors = MutableSharedFlow<Result<Unit>>()
    override val errors: SharedFlow<Result<Unit>> = _errors


    companion object { const val DEBOUNCE_TIME = 2000L }
    private var debounceJob: Job? = null

    private fun debounceAndPatchList() {
        debounceJob?.cancel()
        debounceJob = repositoryScope.launch {
            delay(DEBOUNCE_TIME)
            val localList = localDataSource.getCurrentList()
            val result = remoteDataSource.patchList(localList)
            revision.set((result as? Result.Success)?.value?.revision)
            result.emitErrorsInto(_errors)
            debounceJob = null
        }
    }

    private suspend fun cancelDebounce() {
        if (debounceJob != null) {
            debounceJob?.cancel()
//            val localList = localDataSource.getCurrentList()
//            val result = remoteDataSource.patchList(localList)
//            result.emitErrorsInto(_errors)
            debounceJob = null
        }
    }
}