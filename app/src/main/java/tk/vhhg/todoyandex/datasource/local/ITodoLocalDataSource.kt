package tk.vhhg.todoyandex.datasource.local

import kotlinx.coroutines.flow.Flow
import tk.vhhg.todoyandex.model.TodoItem

interface ITodoLocalDataSource {
    val items: Flow<List<TodoItem>>

    suspend fun getCurrentList(): List<TodoItem>

    suspend fun add(item: TodoItem)

    suspend fun toggle(item: TodoItem)

    fun generateId(): String

    suspend fun update(updatedItem: TodoItem)

    suspend fun remove(item: TodoItem?)

    suspend fun findById(id: String?): TodoItem?

    suspend fun refresh(list: List<TodoItem>)

    suspend fun merge(list: List<TodoItem>)
}