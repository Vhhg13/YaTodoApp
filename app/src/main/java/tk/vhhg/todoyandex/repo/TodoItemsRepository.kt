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
import tk.vhhg.todoyandex.datasource.ITodoRemoteDataSource
import tk.vhhg.todoyandex.datasource.TodoInRamDataSource
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
    private val inRamDataSource: TodoInRamDataSource,
    @DeviceId private val deviceId: String,
    @Connectivity private val connectivity: StateFlow<Boolean>
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
                if (hasNetworkConnection) refresh()
            }
        }
    }

    override fun add(item: TodoItem) {
        inRamDataSource.add(item.updatedBy(deviceId))
        debounceAndPatchList()
    }

    override fun toggle(item: TodoItem) {
        inRamDataSource.toggle(item.updatedBy(deviceId))
        debounceAndPatchList()
    }

    override fun generateId() = inRamDataSource.generateId()

    override fun update(item: TodoItem) {
        inRamDataSource.update(item.updatedBy(deviceId))
        debounceAndPatchList()
    }

    override fun remove(item: TodoItem?) {
        if (item == null) return
        inRamDataSource.remove(item)
        debounceAndPatchList()
    }

    private suspend fun <T> Result<T>.emitErrorsInto(flow: MutableSharedFlow<Result<Unit>>){
        when(this){
            Result.NoInternetConnection -> flow.emit(Result.NoInternetConnection)
            Result.NotFound -> flow.emit(Result.NotFound)
            is Result.RevisionsDoNotMatch -> flow.emit(Result.RevisionsDoNotMatch(this.revision))
            Result.ServerError -> flow.emit(Result.ServerError)
            is Result.Success -> {}
            Result.Unauthorized -> flow.emit(Result.Unauthorized)
            is Result.UnknownError -> flow.emit(Result.UnknownError(this.err))
        }
    }

    override fun refresh() {
        repositoryScope.launch {
            skipDebounce()
            val result = remoteDataSource.getList()
            result.emitErrorsInto(_errors)
            (result as? Result.Success)?.value?.let {
                inRamDataSource.refresh(it)
            }
        }
    }

    override suspend fun findById(id: String?): TodoItem? =
        inRamDataSource.findById(id)

    override val items: Flow<List<TodoItem>> = inRamDataSource.items

    private val _errors = MutableSharedFlow<Result<Unit>>()
    override val errors: SharedFlow<Result<Unit>> = _errors


    companion object { const val DEBOUNCE_TIME = 2000L }
    private var debounceJob: Job? = null

    private fun debounceAndPatchList() {
        debounceJob?.cancel()
        debounceJob = repositoryScope.launch {
            delay(DEBOUNCE_TIME)
            val inRamList = inRamDataSource.items.replayCache[0]
            val result = remoteDataSource.patchList(inRamList)
            result.emitErrorsInto(_errors)
        }
    }

    private suspend fun skipDebounce() {
        if (debounceJob != null) {
            debounceJob?.cancel()
            val inRamList = inRamDataSource.items.replayCache[0]
            val result = remoteDataSource.patchList(inRamList)
            result.emitErrorsInto(_errors)
        }
    }
}