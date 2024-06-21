package tk.vhhg.todoyandex.model

import java.util.Date

data class TodoItem(
    val id: String,
    val isDone: Boolean,
    val body: String,
    val priority: TodoItemPriority = TodoItemPriority.MEDIUM,
    val creationDate: Date,
    val deadline: Date? = null,
    val lastModificationDate: Date? = null
){
}