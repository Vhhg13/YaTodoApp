package tk.vhhg.todoyandex.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for receiving data from network
 * Example value:
 * ```
 * {
 *   "status": "ok",
 *   "list": [
 *     {
 *       "done":false,
 *       "importance":"important",
 *       "last_updated_by":"ab582637-159e-4722-9cc9-42bf4df91839",
 *       "deadline":1720210891612,
 *       "id":"3fcf69b0-812b-4e4c-82a1-0588457b8b8f",
 *       "text":"не удалять",
 *       "changed_at":1720213317340,
 *       "created_at":1720210889764
 *     }
 *   ],
 *   revision: "42"
 * }
 * ```
 */
@Serializable
data class ListDTO(
    @SerialName("status")
    val status: String = "ok",
    @SerialName("list")
    val list: List<TodoItem>,
    @SerialName("revision")
    val revision: Int? = null
)