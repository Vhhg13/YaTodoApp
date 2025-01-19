package tk.vhhg.todoyandex.ui.todolist.viewholders

import androidx.recyclerview.widget.RecyclerView
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.util.DateFormatter
import tk.vhhg.todoyandex.ui.todolist.TodoItemView

class CustomViewTodoViewHolder(private val view: TodoItemView) : RecyclerView.ViewHolder(view), TodoViewHolder {
    override fun onBind(
        item: TodoItem,
        onToggle: (TodoItem) -> Unit,
        onItemClick: (TodoItem?) -> Unit
    ) {
        view.isImportant = when (item.priority) {
            TodoItemPriority.HIGH -> true
            TodoItemPriority.MEDIUM -> null
            TodoItemPriority.LOW -> false
        }

        view.text = item.body
        view.dateText = DateFormatter.toString(item.deadline) ?: ""
        view.setInfoClickListener { onItemClick(item) }
        view.checked = item.isDone
        view.setCheckedClickListener { onToggle(item) }
    }
}