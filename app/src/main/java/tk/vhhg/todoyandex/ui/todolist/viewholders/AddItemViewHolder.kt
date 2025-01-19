package tk.vhhg.todoyandex.ui.todolist.viewholders

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.model.TodoItem

class AddItemViewHolder(view: View, onItemClick: (TodoItem?) -> Unit) :
    RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener { onItemClick(null) }
        view.findViewById<Button>(R.id.plus_button).setOnClickListener { onItemClick(null) }
    }
}