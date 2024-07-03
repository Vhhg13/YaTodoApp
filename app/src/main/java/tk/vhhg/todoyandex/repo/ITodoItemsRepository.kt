package tk.vhhg.todoyandex.repo

import kotlinx.coroutines.flow.Flow
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem


interface ITodoItemsRepository {
    fun add(item: TodoItem)
    fun toggle(itemId: String)
    fun generateId(): String
    fun update(updatedItem: TodoItem)
    fun remove(item: TodoItem?)
    suspend fun findById(id: String?): Result<TodoItem>

    val items: Flow<List<TodoItem>>
    val errors: Flow<Throwable>
}