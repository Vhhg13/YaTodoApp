package tk.vhhg.todoyandex.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.util.DateConverter

@Database(entities = [TodoItem::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoItemDao
}