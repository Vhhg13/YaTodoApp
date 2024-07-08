package tk.vhhg.todoyandex.ui.edittask

import tk.vhhg.todoyandex.model.TodoItemPriority

/**
 * `data class` used to represent a state of the [task editing screen][EditTaskFragment]
 */
data class EditTaskState(
    val body: String = "",
    val priority: TodoItemPriority = TodoItemPriority.MEDIUM,
    val deadline: Long? = null
)