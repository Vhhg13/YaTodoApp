package tk.vhhg.todoyandex.ui.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.repo.ITodoItemsRepository
import java.util.Date

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

    val taskId: String? = savedStateHandle["taskId"]
    private val _uiState = MutableStateFlow(EditTaskState())
    val uiState = _uiState.asStateFlow()
    private var item: TodoItem? = null
    val initialBodyContents: String?
        get() = item?.body

    init {
        viewModelScope.launch {
            item = repo.findById(taskId)
            mutableListOf<Any>().toMutableList()
        }
        item?.let {
            _uiState.value = EditTaskState(it.priority, it.deadline?.time)
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
        _uiState.update { state ->
            state.copy(
                deadline = if (isChecked) {
                    item?.deadline?.time ?: Date().time
                } else {
                    null
                }
            )
        }
    }

    fun save(body: String) {
        if (item == null) {
            repo.add(
                TodoItem(
                    id = repo.generateId(),
                    isDone = false,
                    body = body,
                    priority = uiState.value.priority,
                    creationDate = Date(),
                    deadline = uiState.value.deadline?.let { Date(it) },
                    lastModificationDate = Date()
                )
            )
        } else {
            repo.update(
                item!!.copy(
                    body = body,
                    priority = uiState.value.priority,
                    deadline = uiState.value.deadline?.let { Date(it) },
                    lastModificationDate = Date()
                )
            )
        }
    }

    fun delete() = repo.remove(item)
}