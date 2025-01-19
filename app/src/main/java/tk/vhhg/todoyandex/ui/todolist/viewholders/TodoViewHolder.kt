package tk.vhhg.todoyandex.ui.todolist.viewholders

import tk.vhhg.todoyandex.model.TodoItem

interface TodoViewHolder {
    fun onBind(
        item: TodoItem,
        onToggle: (TodoItem) -> Unit,
        onItemClick: (TodoItem?) -> Unit
    )
}