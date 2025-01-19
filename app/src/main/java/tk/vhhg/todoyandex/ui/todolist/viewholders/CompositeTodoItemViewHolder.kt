package tk.vhhg.todoyandex.ui.todolist.viewholders

import android.graphics.Paint
import android.widget.ToggleButton
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.TodolistItemBinding
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.util.DateFormatter

class CompositeTodoItemViewHolder(private val binding: TodolistItemBinding) :
    RecyclerView.ViewHolder(binding.root), TodoViewHolder {
    private var defaultBodyColor: Int? = null

    override fun onBind(
        item: TodoItem, onToggle: (TodoItem) -> Unit, onItemClick: (TodoItem?) -> Unit
    ) = with(binding) {
        bindBody(item)
        bindSubhead(item)
        bindToggleButton(item, onToggle)

        checkbox.setOnClickListener { onToggle(item) }
        root.setOnClickListener { onItemClick(item) }
        info.setOnClickListener { onItemClick(item) }
    }.let {}

    private fun bindToggleButton(
        item: TodoItem, onToggle: (TodoItem) -> Unit
    ) = with(binding) {
        val backgroundResource =
            if (item.priority == TodoItemPriority.HIGH) R.drawable.red_checkbox else R.drawable.green_checkbox
        val toggleButton = checkbox.getChildAt(0) as ToggleButton
        toggleButton.setBackgroundResource(backgroundResource)
        toggleButton.isChecked = item.isDone
        toggleButton.setOnClickListener { onToggle(item) }
    }

    private fun bindSubhead(item: TodoItem) = with(binding) {
        subhead.isGone = item.deadline == null
        subhead.text = DateFormatter.toString(item.deadline)
    }

    private fun bindBody(item: TodoItem) = with(binding) {
        body.paintFlags = if (item.isDone) {
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
        val stringResource = when (item.priority) {
            TodoItemPriority.HIGH -> R.string.high_priority_item
            TodoItemPriority.MEDIUM -> R.string.medium_priority_item
            TodoItemPriority.LOW -> R.string.low_priority_item
        }
        body.text = root.context.getString(stringResource, item.body)
    }
}