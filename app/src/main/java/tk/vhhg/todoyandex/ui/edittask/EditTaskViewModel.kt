package tk.vhhg.todoyandex.ui.edittask

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.repo.ITodoItemsRepository
import java.util.Date

/**
 * ViewModel for the [task editing screen][EditTaskFragment].
 * Holds the current state for the screen and handles UI logic.
 */
class EditTaskViewModel(
    private val repo: ITodoItemsRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {

    val item: TodoItem? = savedStateHandle["todoItem"]
    private val _uiState = MutableStateFlow(EditTaskState())
    val uiState = _uiState.asStateFlow()

    init {
        item?.let {
            _uiState.value = EditTaskState(it.body, it.priority, it.deadline?.time)
        }
    }

    fun changePriority(priority: TodoItemPriority) {
        _uiState.update {
            it.copy(priority = priority)
        }
    }

    fun changeDeadline(deadline: Long?) {
        _uiState.update {
            it.copy(deadline = deadline)
        }
    }

    fun toggleDeadline(isChecked: Boolean) {
        val newDeadline = if (isChecked) {
            item?.deadline?.time ?: Date().time
        } else {
            null
        }
        _uiState.update { state ->
            state.copy(deadline = newDeadline)
        }
    }

    fun save() {
        if (item != null) {
            update(item)
        } else {
            add()
        }
    }

    private fun update(item: TodoItem) {
        repo.update(
            item.copy(
                body = uiState.value.body,
                priority = uiState.value.priority,
                deadline = uiState.value.deadline?.let { Date(it) },
                lastModificationDate = Date()
            )
        )
    }

    private fun add() {
        repo.add(
            TodoItem(
                id = repo.generateId(),
                isDone = false,
                body = uiState.value.body,
                priority = uiState.value.priority,
                creationDate = Date(),
                deadline = uiState.value.deadline?.let { Date(it) },
                lastModificationDate = Date()
            )
        )
    }

    fun changeBody(body: String) {
        _uiState.update { state ->
            state.copy(body = body)
        }
    }

    fun delete() = repo.remove(item)

    class Factory @AssistedInject constructor(
        private val repo: ITodoItemsRepository,
        @Assisted("todoItem") private val item: TodoItem?,
        @Assisted("owner") private val owner: SavedStateRegistryOwner,
        @Assisted("args") private val defaultArgs: Bundle?
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            handle["todoItem"] = item
            return EditTaskViewModel(repo, handle) as T
        }

        @AssistedFactory
        interface AFactory {
            fun create(
                @Assisted("todoItem") item: TodoItem?,
                @Assisted("owner") owner: SavedStateRegistryOwner,
                @Assisted("args") defaultArgs: Bundle? = null
            ): Factory
        }
    }

}