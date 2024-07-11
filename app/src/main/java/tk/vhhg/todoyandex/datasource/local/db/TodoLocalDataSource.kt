package tk.vhhg.todoyandex.datasource.local.db

import android.util.Log
import kotlinx.coroutines.flow.Flow
import tk.vhhg.todoyandex.datasource.local.ITodoLocalDataSource
import tk.vhhg.todoyandex.model.TodoItem
import java.util.UUID
import javax.inject.Inject
import kotlin.math.sign

class TodoLocalDataSource @Inject constructor(private val dao: TodoItemDao) : ITodoLocalDataSource {
    override val items: Flow<List<TodoItem>>
        get() = dao.getObservableTodoList()

    override suspend fun getCurrentList(): List<TodoItem> {
        return dao.getCurrentTodoList().also { Log.d("trace list (lds)", it.toString()) }
    }

    override suspend fun add(item: TodoItem) {
        dao.add(listOf(item))
    }

    override suspend fun toggle(item: TodoItem) {
        update(item.copy(isDone = !item.isDone))
    }

    override fun generateId() = UUID.randomUUID().toString()

    override suspend fun update(updatedItem: TodoItem) {
        dao.updateTodo(updatedItem)
    }

    override suspend fun remove(item: TodoItem?) {
        item?.let { dao.remove(it) }
    }

    override suspend fun findById(id: String?): TodoItem? {
        return id?.let { dao.findById(id) }
    }

    override suspend fun refresh(list: List<TodoItem>) {
        merge(list)
    }

    override suspend fun merge(list: List<TodoItem>) {
        val curMap = getCurrentList().associateBy { it.id }
        val otherMap = list.associateBy { it.id }
        val ids = curMap.keys + otherMap.keys
        Log.d("trace list (ids)", ids.toString())
        for(id in ids){
            val curItem = curMap[id]
            val otherItem = otherMap[id] ?: continue
            if(curItem == null){
                dao.add(listOf(otherItem))
            }else if(otherItem.lastChange() > curItem.lastChange()){
                dao.updateTodo(otherItem)
            }
        }
        dao.getCurrentTodoList().also { Log.d("trace list (after merge)", it.toString()) }
    }
    private fun TodoItem.lastChange() = lastModificationDate ?: creationDate
}