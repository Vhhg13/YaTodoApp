package tk.vhhg.todoyandex.ui.todolist

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _uiState = MutableStateFlow(TodoListUiState(listOf()))
    val uiState = _uiState.asStateFlow()

    val errors = repo.errors

    init {
        collectWithViewModelScope(repo.items) { list ->
            _uiState.update {
                it.copy(list = list)
            }
        }
        collectWithViewModelScope(repo.errors) { error ->
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
        repo.sync()
    }

    private fun <T> collectWithViewModelScope(flow: Flow<T>, block: (T) -> Unit) {
        viewModelScope.launch {
            flow.collect(block)
        }
    }

    class Factory @AssistedInject constructor(
        private val repo: ITodoItemsRepository,
        @Assisted("owner") private val owner: SavedStateRegistryOwner,
        @Assisted("args") private val defaultArgs: Bundle?
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return TodoListViewModel(repo, handle) as T
        }

        @AssistedFactory
        interface AFactory {
            fun create(
                @Assisted("owner") owner: SavedStateRegistryOwner,
                @Assisted("args") defaultArgs: Bundle? = null
            ): Factory
        }
    }
}