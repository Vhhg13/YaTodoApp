package tk.vhhg.todoyandex.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import tk.vhhg.todoyandex.model.TodoItem

interface ITodoItemsRepository {
    fun add(item: TodoItem)
    fun toggle(itemId: String)
    fun generateId(): String
    fun update(updatedItem: TodoItem)
    fun remove(item: TodoItem?)
    suspend fun findById(id: String?): TodoItem?

    val items: Flow<List<TodoItem>>
}