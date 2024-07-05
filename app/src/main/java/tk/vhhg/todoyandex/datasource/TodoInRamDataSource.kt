package tk.vhhg.todoyandex.datasource

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tk.vhhg.todoyandex.model.TodoItem
import java.util.UUID

/**
 * Temporary class for storing data locally without a database
 */
class TodoInRamDataSource {
    private var data = mutableListOf<TodoItem>()
    private val _items = MutableSharedFlow<List<TodoItem>>(replay = 1)

    val items: SharedFlow<List<TodoItem>> = _items.asSharedFlow()

    fun add(item: TodoItem) {
        data.add(item)
        _items.tryEmit(data.toMutableList())
    }


    fun toggle(item: TodoItem) : TodoItem {
        val indexToBeChanged = data.indexOfFirst { it.id == item.id }
        data[indexToBeChanged] = item.copy(isDone = !item.isDone)
        _items.tryEmit(data.toMutableList())
        return data[indexToBeChanged]
    }

    fun generateId() = UUID.randomUUID().toString()

    fun update(updatedItem: TodoItem): TodoItem {
        val indexToBeChanged = data.indexOfFirst { it.id == updatedItem.id }
        data[indexToBeChanged] = updatedItem
        _items.tryEmit(data.toMutableList())
        return updatedItem
    }

    fun remove(item: TodoItem?): TodoItem? {
        data.remove(item)
        _items.tryEmit(data.toMutableList())
        return item
    }

    fun findById(id: String?) : TodoItem? = data.find { it.id == id }

    fun refresh(list: List<TodoItem>) {
        data = list.toMutableList()
        _items.tryEmit(data.toMutableList())
    }
}