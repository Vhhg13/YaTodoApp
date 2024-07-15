package tk.vhhg.todoyandex.datasource.local.inram

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tk.vhhg.todoyandex.datasource.local.ITodoLocalDataSource
import tk.vhhg.todoyandex.di.TodoAppScope
import tk.vhhg.todoyandex.model.TodoItem
import java.util.UUID
import javax.inject.Inject

/**
 * Temporary class for storing data locally without a database
 */
@TodoAppScope
class TodoInRamDataSource @Inject constructor() : ITodoLocalDataSource {
    private var data = mutableListOf<TodoItem>()
    private val _items = MutableSharedFlow<List<TodoItem>>(replay = 1)

    override val items: SharedFlow<List<TodoItem>> = _items.asSharedFlow()

    override suspend fun merge(list: List<TodoItem>) {
        data = list.toMutableList()
        _items.emit(data)
    }

    override suspend fun getCurrentList(): List<TodoItem> {
        return _items.replayCache[0]
    }

    override suspend fun add(item: TodoItem) {
        data.add(item)
        _items.tryEmit(data.toMutableList())
    }


    override suspend fun toggle(item: TodoItem) {
        val indexToBeChanged = data.indexOfFirst { it.id == item.id }
        data[indexToBeChanged] = item.copy(isDone = !item.isDone)
        _items.tryEmit(data.toMutableList())
    }

    override fun generateId() = UUID.randomUUID().toString()

    override suspend fun update(updatedItem: TodoItem) {
        val indexToBeChanged = data.indexOfFirst { it.id == updatedItem.id }
        data[indexToBeChanged] = updatedItem
        _items.tryEmit(data.toMutableList())
    }

    override suspend fun remove(item: TodoItem?) {
        data.remove(item)
        _items.tryEmit(data.toMutableList())
    }

    override suspend fun findById(id: String?) : TodoItem? = data.find { it.id == id }

    override suspend fun refresh(list: List<TodoItem>) {
        data = list.toMutableList()
        _items.tryEmit(data.toMutableList())
    }
}