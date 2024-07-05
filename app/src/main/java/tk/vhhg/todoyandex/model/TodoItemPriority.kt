package tk.vhhg.todoyandex.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a todo item priority: [TodoItem.priority]
 */
@Serializable
enum class TodoItemPriority{
    @SerialName("important")
    HIGH,
    @SerialName("basic")
    MEDIUM,
    @SerialName("low")
    LOW
}