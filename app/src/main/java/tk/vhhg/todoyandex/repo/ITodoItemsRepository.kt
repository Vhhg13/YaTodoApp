package tk.vhhg.todoyandex.repo

import kotlinx.coroutines.flow.Flow
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem

/**
 * A single place for storing and retrieving [TodoItem]s
 */
interface ITodoItemsRepository {
    fun add(item: TodoItem)
    fun toggle(item: TodoItem)
    fun generateId(): String
    fun update(item: TodoItem)
    fun remove(item: TodoItem?)
    fun sync()
    suspend fun findById(id: String?): TodoItem?

    val items: Flow<List<TodoItem>>
    val errors: Flow<Result<Unit>>
}