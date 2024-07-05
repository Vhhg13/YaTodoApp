package tk.vhhg.todoyandex.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tk.vhhg.todoyandex.util.DateSerializer
import java.util.Date

/**
 * DTO that represents a todo item.
 * Example value:
 * ```
 * {
 *   "done":false,
 *   "importance":"important",
 *   "last_updated_by":"ab582637-159e-4722-9cc9-42bf4df91839",
 *   "deadline":1720210891612,
 *   "id":"3fcf69b0-812b-4e4c-82a1-0588457b8b8f",
 *   "text":"не удалять",
 *   "changed_at":1720213317340,
 *   "created_at":1720210889764
 * }
 * ```
 */
@Serializable
data class TodoItem(
    @SerialName("id")
    val id: String,
    @SerialName("done")
    val isDone: Boolean,
    @SerialName("text")
    val body: String,
    @SerialName("importance")
    val priority: TodoItemPriority = TodoItemPriority.MEDIUM,
    @SerialName("created_at") @Serializable(with = DateSerializer::class)
    val creationDate: Date,
    @SerialName("deadline") @Serializable(with = DateSerializer::class)
    val deadline: Date? = null,
    @SerialName("changed_at") @Serializable(with = DateSerializer::class)
    val lastModificationDate: Date? = null,
    @SerialName("last_updated_by")
    var lastUpdatedBy: String? = null,
    @SerialName("color")
    var color: String? = null
): Parcelable{
    fun updatedBy(uuid: String) = copy(lastUpdatedBy = uuid, lastModificationDate = Date())
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        TodoItemPriority.valueOf(parcel.readString()!!),
        Date(parcel.readLong()),
        parcel.readLong().let { if(it == 0L) null else Date(it) },
        parcel.readLong().let { if(it == 0L) null else Date(it) },
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByte(if (isDone) 1 else 0)
        parcel.writeString(body)
        parcel.writeString(priority.name)
        parcel.writeLong(creationDate.time)
        parcel.writeLong(deadline?.time ?: 0)
        parcel.writeLong(lastModificationDate?.time ?: 0)
        parcel.writeString(lastUpdatedBy)
        parcel.writeString(color)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TodoItem> {
        override fun createFromParcel(parcel: Parcel): TodoItem {
            return TodoItem(parcel)
        }

        override fun newArray(size: Int): Array<TodoItem?> {
            return arrayOfNulls(size)
        }
    }
}