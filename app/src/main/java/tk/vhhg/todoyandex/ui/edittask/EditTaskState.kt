package tk.vhhg.todoyandex.ui.edittask

import tk.vhhg.todoyandex.model.TodoItemPriority

data class EditTaskState(
    val priority: TodoItemPriority = TodoItemPriority.MEDIUM,
    val deadline: Long? = null
)