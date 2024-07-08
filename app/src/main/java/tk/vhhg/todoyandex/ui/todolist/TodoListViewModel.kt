package tk.vhhg.todoyandex.ui.todolist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.repo.ITodoItemsRepository

/**
 * ViewModel for the [todo list screen][TodoListFragment].
 * Holds the current state for the screen and handles UI logic.
 */
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

    private val _uiState = MutableStateFlow(TodoListUiState(listOf()))
    val uiState = _uiState.asStateFlow()

    val errors = repo.errors

    init {
        collectWithViewModelScope(repo.items){ list ->
            _uiState.update {
                it.copy(list = list, isLoading = false)
            }
        }
        collectWithViewModelScope(repo.errors){ error ->
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun toggleDoneTasksVisibility() {
        val areDoneTasksVisibleNow = _uiState.value.areDoneTasksVisible
        _uiState.update {
            it.copy(areDoneTasksVisible = !areDoneTasksVisibleNow)
        }
    }

    fun toggle(todoItem: TodoItem) {
        repo.toggle(todoItem)
    }
    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        repo.refresh()
    }
    private fun <T> collectWithViewModelScope(flow: Flow<T>, block: (T) -> Unit){
        viewModelScope.launch {
            flow.collect(block)
        }
    }
}