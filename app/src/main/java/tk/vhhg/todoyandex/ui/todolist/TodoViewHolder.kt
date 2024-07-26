package tk.vhhg.todoyandex.ui.todolist

import android.graphics.Paint
import android.view.View
import android.widget.ToggleButton
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.TodolistItemBinding
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority

class TodoViewHolder(private val binding: TodolistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private var defaultBodyColor: Int? = null

    fun onBind(
        item: TodoItem,
        onToggle: (TodoItem) -> Unit,
        onItemClick: (TodoItem?) -> Unit
    ) {
        binding.apply {
            configureAccessibility(root, item, onToggle)
            bindBody(item)
            bindSubhead(item)
            bindToggleButton(item, onToggle)

            checkbox.setOnClickListener { onToggle(item) }
            root.setOnClickListener { onItemClick(item) }
            info.setOnClickListener { onItemClick(item) }
        }
    }

    private fun configureAccessibility(root: View, item: TodoItem, onToggle: (TodoItem) -> Unit){
        root.describeTodoItem(item)
        ViewCompat.addAccessibilityAction(
            root,
            root.context.getString(R.string.toggle_done)
        ) { _, _ ->
            onToggle(item)
            root.describeTodoItem(item)
            true
        }
    }

    private fun bindToggleButton(
        item: TodoItem,
        onToggle: (TodoItem) -> Unit
    ) {
        binding.apply {
            val backgroundResource =
                if (item.priority == TodoItemPriority.HIGH) R.drawable.red_checkbox else R.drawable.green_checkbox
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
            if (item.isDone) {
                defaultBodyColor = defaultBodyColor ?: body.currentTextColor
                body.setTextColor(subhead.currentTextColor)
            } else {
                defaultBodyColor?.let { body.setTextColor(defaultBodyColor!!) }
            }
            val stringResource = when (item.priority) {
                TodoItemPriority.HIGH -> R.string.high_priority_item
                TodoItemPriority.MEDIUM -> R.string.medium_priority_item
                TodoItemPriority.LOW -> R.string.low_priority_item
            }
            body.text = root.context.getString(stringResource, item.body)
        }
    }

    private fun View.describeTodoItem(item: TodoItem) {
        val sb = StringBuilder()
        sb.append(context.getString(if (item.isDone) R.string.done_content_description else R.string.undone_content_description))
        sb.append(' ')
        sb.append(item.body)
        sb.append(". ")
        sb.append(
            context.getString(
                R.string.priority_content_description,
                context.getString(describePriority(item.priority))
            )
        )
        if (item.deadline != null) {
            sb.append(". ")
            sb.append(
                context.getString(
                    R.string.deadline_in_list,
                    DateFormatter.sdf.format(item.deadline)
                )
            )
        }
        contentDescription = sb.toString()
    }

    private fun describePriority(priority: TodoItemPriority) =
        when (priority) {
            TodoItemPriority.HIGH -> R.string.high_priority_content_desciption
            TodoItemPriority.MEDIUM -> R.string.medium_priority_content_description
            TodoItemPriority.LOW -> R.string.low_priority_content_description
        }
}

