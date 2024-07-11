package tk.vhhg.todoyandex.ui.todolist

import tk.vhhg.todoyandex.model.TodoItem

/**
 * `data class` that represents a state for the [todo list screen][TodoListFragment]
 */
data class TodoListUiState(
    val list: List<TodoItem>,
    val areDoneTasksVisible: Boolean = true,
    val isLoading: Boolean = false
) {
    val tasksDone: Int get() = list.count { it.isDone }
    val filteredList: List<TodoItem> = list.filter { !it.isDone or areDoneTasksVisible }
}
