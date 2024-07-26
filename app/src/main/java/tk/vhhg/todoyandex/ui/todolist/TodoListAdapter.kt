package tk.vhhg.todoyandex.ui.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.AddTodolistItemBinding
import tk.vhhg.todoyandex.databinding.TodolistItemBinding
import tk.vhhg.todoyandex.model.TodoItem

/**
 * Adapter used to display [TodoItem]s in a list.
 *
 * I'm too scared to edit any code here, RecyclerView just works :,)
 */
class TodoListAdapter(private val onToggle: (TodoItem) -> Unit, private val onItemClick: (TodoItem?) -> Unit) :
    ListAdapter<TodoItem, ViewHolder>(DIFF) {

    companion object DIFF : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem) = oldItem == newItem
        const val ADD_BUTTON = 1
        const val TODO_ITEM = 0
    }

    override fun getItemCount() = super.getItemCount()+1

    override fun getItemViewType(position: Int) = if(position == super.getItemCount()) ADD_BUTTON else TODO_ITEM

    class AddItemViewHolder(view: View, onItemClick: (TodoItem?) -> Unit) : ViewHolder(view){
        init {
            view.setOnClickListener { onItemClick(null) }
            view.findViewById<Button>(R.id.plus_button).setOnClickListener{ onItemClick(null) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == ADD_BUTTON) {
            AddItemViewHolder(AddTodolistItemBinding.inflate(inflater, parent, false).root, onItemClick)
        } else {
            TodoViewHolder(TodolistItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? TodoViewHolder)?.onBind(
            item = getItem(position),
            onToggle = onToggle,
            onItemClick = onItemClick
        )
    }
}