package tk.vhhg.todoyandex.datasource.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tk.vhhg.todoyandex.model.TodoItem

@Dao
interface TodoItemDao{
    @Delete
    suspend fun remove(item: TodoItem)

    @Insert
    suspend fun add(item: List<TodoItem>)

    @Query("SELECT * FROM TodoItems")
    fun getObservableTodoList(): Flow<List<TodoItem>>

    @Query("SELECT * FROM TodoItems ORDER BY id")
    suspend fun getCurrentTodoList(): List<TodoItem>

    @Update
    fun updateTodo(item: TodoItem)

    @Query("SELECT * FROM TodoItems WHERE id = :id")
    suspend fun findById(id: String): TodoItem
}