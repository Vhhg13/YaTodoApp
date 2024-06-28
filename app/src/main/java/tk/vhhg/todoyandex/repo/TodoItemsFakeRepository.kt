package tk.vhhg.todoyandex.repo

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority
import java.util.Date
import java.util.UUID

class TodoItemsFakeRepository : ITodoItemsRepository {
    private val handler = CoroutineExceptionHandler { _, throwable ->
        _errors.tryEmit(throwable)
    }
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)
    // вынесение бизнес логики в background ниже в этом файле
    private val _items = MutableStateFlow<List<TodoItem>>(buildList {
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

    override val items: StateFlow<List<TodoItem>> = _items.asStateFlow()

    override fun add(item: TodoItem) {
        repositoryScope.launch {
            _items.update {
                it.toMutableList().apply {
                    add(item)
                }
            }
        }
    }


    override fun toggle(itemId: String) {
        repositoryScope.launch {
            _items.update { oldList ->
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
        val currentList = _items.value
        val updatedList = currentList.toMutableList()
        updatedList.replaceAll { todoItem ->
            if (updatedItem.id == todoItem.id) updatedItem else todoItem
        }
        _items.value = updatedList
    }

    override fun remove(item: TodoItem?) {
        _items.value = _items.value.filter { it.id != item?.id }
    }

    override suspend fun findById(id: String?) : Result<TodoItem> {
        val item = _items.value.find { it.id == id }
        return if(item != null){
            Result.Success(item)
        }else{
            Result.Error(NoSuchElementException())
        }
    }

    private val _errors = MutableSharedFlow<Throwable>(3)
    override val errors: Flow<Throwable> = _errors.asSharedFlow()
}