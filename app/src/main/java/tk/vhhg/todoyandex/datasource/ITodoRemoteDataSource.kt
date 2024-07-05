package tk.vhhg.todoyandex.datasource

import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.Result
import java.util.UUID

/**
 * Used for sending and receiving data from the network
 */
interface ITodoRemoteDataSource {
    suspend fun getList(): Result<List<TodoItem>>
    suspend fun patchList(lst: List<TodoItem>): Result<List<TodoItem>>
    suspend fun postToList(item: TodoItem): Result<TodoItem>

    suspend fun getById(id: UUID): Result<TodoItem>
    suspend fun putById(item: TodoItem): Result<TodoItem>
    suspend fun deleteById(id: String): Result<TodoItem>
}