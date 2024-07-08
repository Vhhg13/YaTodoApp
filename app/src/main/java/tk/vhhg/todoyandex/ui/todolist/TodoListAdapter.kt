package tk.vhhg.todoyandex.ui.todolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.AddTodolistItemBinding
import tk.vhhg.todoyandex.databinding.TodolistItemBinding
import tk.vhhg.todoyandex.model.TodoItemPriority
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

    class TodoViewHolder(private val binding: TodolistItemBinding) : ViewHolder(binding.root) {
        private var defaultBodyColor: Int? = null

        fun onBind(
            item: TodoItem,
            onToggle: (TodoItem) -> Unit,
            onItemClick: (TodoItem?) -> Unit
        ) {
            binding.apply {
                bindBody(item)
                bindSubhead(item)
                bindToggleButton(item, onToggle)

                checkbox.setOnClickListener { onToggle(item) }
                root.setOnClickListener { onItemClick(item) }
                info.setOnClickListener { onItemClick(item) }
            }
        }

        private fun bindToggleButton(
            item: TodoItem,
            onToggle: (TodoItem) -> Unit
        ) {
            binding.apply {
                val backgroundResource = if (item.priority == TodoItemPriority.HIGH) R.drawable.red_checkbox else R.drawable.green_checkbox
                val toggleButton = checkbox.getChildAt(0) as ToggleButton
                toggleButton.setBackgroundResource(backgroundResource)
                toggleButton.isChecked = item.isDone
                toggleButton.setOnClickListener { onToggle(item) }
            }
        }

        private fun bindSubhead(item: TodoItem) {
            binding.apply {
                subhead.isGone = item.deadline == null
                subhead.text = item.deadline?.let { date -> DateFormatter.sdf.format(date) }
            }
        }

        private fun bindBody(item: TodoItem) {
            binding.apply {
                body.paintFlags =
                    if (item.isDone) {
                        body.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        body.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                // Сделать зачёркнутый текст серым
                if (item.isDone) {
                    defaultBodyColor = defaultBodyColor ?: body.currentTextColor
                    body.setTextColor(subhead.currentTextColor)
                } else {
                    defaultBodyColor?.let { body.setTextColor(defaultBodyColor!!) }
                }
                val stringResource = when(item.priority){
                    TodoItemPriority.HIGH -> R.string.high_priority_item
                    TodoItemPriority.MEDIUM -> R.string.medium_priority_item
                    TodoItemPriority.LOW -> R.string.low_priority_item
                }
                body.text = root.context.getString(stringResource, item.body)
            }
        }


    }

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