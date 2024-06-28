package tk.vhhg.todoyandex.ui.todolist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.repo.ITodoItemsRepository

class TodoListViewModel(
    private val repo: ITodoItemsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()
                return TodoListViewModel((application as App).repo, savedStateHandle) as T
            }
        }
    }

    private val _areDoneTasksVisible = MutableStateFlow(true)
    val areDoneTasksVisible = _areDoneTasksVisible.asStateFlow()

    private val _items = MutableStateFlow(listOf<TodoItem>())
    val items: StateFlow<List<TodoItem>> = _items.asStateFlow()

    private val _tasksDone = MutableStateFlow(0)
    val tasksDone: StateFlow<Int> = _tasksDone.asStateFlow()

    init {
        collectItems()
        collectDoneTasksCount()
    }

    private fun collectItems() {
        viewModelScope.launch {
            repo.items
                .combine(_areDoneTasksVisible) { todoItemList, doneTasksVisibility ->
                    todoItemList.filter { !it.isDone or doneTasksVisibility }
                }
                .collect { todoList ->
                    _items.value = todoList
                }
        }
    }

    private fun collectDoneTasksCount() {
        viewModelScope.launch {
            repo.items.collect { todoList ->
                _tasksDone.value = todoList.count { it.isDone }
            }
        }
    }

    fun toggleDoneTasksVisibility() {
        _areDoneTasksVisible.update { !it }
    }

    fun toggle(todoItemId: String) {
        repo.toggle(todoItemId)
    }


}