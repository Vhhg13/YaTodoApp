package tk.vhhg.todoyandex.ui.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import tk.vhhg.todoyandex.App
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
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()
                return EditTaskViewModel((application as App).repo, savedStateHandle) as T
            }
        }
    }

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
            repo.update(item.copy(
                body = uiState.value.body,
                priority = uiState.value.priority,
                deadline = uiState.value.deadline?.let { Date(it) },
                lastModificationDate = Date()
            ))
            return
        }

        repo.add(TodoItem(
            id = repo.generateId(),
            isDone = false,
            body = uiState.value.body,
            priority = uiState.value.priority,
            creationDate = Date(),
            deadline = uiState.value.deadline?.let { Date(it) },
            lastModificationDate = Date()
        ))
    }

    fun changeBody(body: String){
        _uiState.update { state ->
            state.copy(body = body)
        }
    }

    fun delete() = repo.remove(item)
}