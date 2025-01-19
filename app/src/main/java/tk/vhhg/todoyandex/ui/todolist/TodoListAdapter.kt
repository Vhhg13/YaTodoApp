package tk.vhhg.todoyandex.ui.todolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import tk.vhhg.todoyandex.databinding.AddTodolistItemBinding
import tk.vhhg.todoyandex.databinding.TodolistItemBinding
import tk.vhhg.todoyandex.model.ListItemType
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.ui.todolist.viewholders.AddItemViewHolder
import tk.vhhg.todoyandex.ui.todolist.viewholders.CompositeTodoItemViewHolder
import tk.vhhg.todoyandex.ui.todolist.viewholders.CustomViewTodoViewHolder
import tk.vhhg.todoyandex.ui.todolist.viewholders.TodoViewHolder

/**
 * Adapter used to display [TodoItem]s in a list.
 *
 * I'm too scared to edit any code here, RecyclerView just works :,)
 */
class TodoListAdapter(
    private val onToggle: (TodoItem) -> Unit,
    private val onItemClick: (TodoItem?) -> Unit,
    private val preferredItemType: ListItemType = ListItemType.NONCUSTOM
) : ListAdapter<TodoItem, ViewHolder>(DIFF) {

    companion object DIFF : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem) = oldItem == newItem
        const val ADD_BUTTON = 1
        const val TODO_ITEM = 0
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItemViewType(position: Int) =
        if (position == super.getItemCount()) ADD_BUTTON else TODO_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ADD_BUTTON) {
            AddItemViewHolder(
                AddTodolistItemBinding.inflate(inflater, parent, false).root,
                onItemClick
            )
        } else if (preferredItemType == ListItemType.NONCUSTOM) {
            CompositeTodoItemViewHolder(TodolistItemBinding.inflate(inflater, parent, false))
        } else {
            CustomViewTodoViewHolder(TodoItemView(parent.context))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? TodoViewHolder)?.onBind(
            item = getItem(position), onToggle = onToggle, onItemClick = onItemClick
        )
    }
}