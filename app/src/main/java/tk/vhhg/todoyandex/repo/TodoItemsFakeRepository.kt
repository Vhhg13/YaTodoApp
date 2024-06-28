package tk.vhhg.todoyandex.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority
import java.util.Date
import java.util.UUID

class TodoItemsFakeRepository : ITodoItemsRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val data = MutableStateFlow<List<TodoItem>>(buildList {
        var id = 0
        for (checked in listOf(true, false, true, false)) {
            for (priority in TodoItemPriority.entries) {
                for (date in listOf(Date(), null, Date(), null)) {
                    add(
                        TodoItem(
                            id = "${id++}",
                            isDone = checked,
                            body = "$checked, $priority, ${date?.let { "date" }}",
                            priority = priority,
                            creationDate = date ?: Date(),
                            deadline = date
                        )
                    )
                }
            }
        }
    })

    override val items: StateFlow<List<TodoItem>> = data.asStateFlow()

    override fun add(item: TodoItem) {
        repositoryScope.launch {
            data.update {
                it.toMutableList().apply {
                    add(item)
                }
            }
        }
    }


    override fun toggle(itemId: String) {
        repositoryScope.launch {
            data.update { oldList ->
                oldList.toMutableList().apply {
                    replaceAll { oldItem ->
                        if (itemId == oldItem.id)
                            oldItem.copy(isDone = !oldItem.isDone)
                        else
                            oldItem
                    }
                }
            }
        }

    }

    override fun generateId() = UUID.randomUUID().toString()

    override fun update(updatedItem: TodoItem) {
        val currentList = data.value
        val updatedList = currentList.toMutableList()
        updatedList.replaceAll { todoItem ->
            if (updatedItem.id == todoItem.id) updatedItem else todoItem
        }
        data.value = updatedList
    }

    override fun remove(item: TodoItem?) {
        data.value = data.value.filter { it.id != item?.id }
    }

    override suspend fun findById(id: String?) = data.value.find { it.id == id }
}