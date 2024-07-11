package tk.vhhg.todoyandex.datasource.local.db

import android.util.Log
import kotlinx.coroutines.flow.Flow
import tk.vhhg.todoyandex.datasource.local.ITodoLocalDataSource
import tk.vhhg.todoyandex.di.DeviceId
import tk.vhhg.todoyandex.model.TodoItem
import java.util.UUID
import javax.inject.Inject

class TodoLocalDataSource @Inject constructor(
    private val dao: TodoItemDao,
    @DeviceId private val deviceId: String
) : ITodoLocalDataSource {
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
        val localMap = getCurrentList().associateBy { it.id }
        val remoteMap = list.associateBy { it.id }
        val ids = localMap.keys + remoteMap.keys
        for (id in ids) {
            val localItem = localMap[id]
            val remoteItem = remoteMap[id]
            if (localItem != null && remoteItem != null) {
                if (remoteItem.lastChange() > localItem.lastChange())
                    dao.updateTodo(remoteItem)
            } else if (localItem != null && remoteItem == null) {
                if(localItem.lastUpdatedBy != deviceId){
                    dao.remove(localItem)
                }
            } else if (localItem == null && remoteItem != null) {
                if (remoteItem.lastUpdatedBy != deviceId) {
                    dao.add(listOf(remoteItem))
                }
            } else {
                throw IllegalStateException("Impossible situation")
            }
        }
        dao.getCurrentTodoList().also { Log.d("trace list (after merge)", it.toString()) }
    }

    private fun TodoItem.lastChange() = lastModificationDate ?: creationDate
}