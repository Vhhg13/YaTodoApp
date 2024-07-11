package tk.vhhg.todoyandex.datasource.remote

import tk.vhhg.todoyandex.model.ListDTO
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.Result
import java.util.UUID

/**
 * Used for sending and receiving data from the network
 */
interface ITodoRemoteDataSource {
    suspend fun getList(): Result<ListDTO>
    suspend fun patchList(lst: List<TodoItem>): Result<ListDTO>
}