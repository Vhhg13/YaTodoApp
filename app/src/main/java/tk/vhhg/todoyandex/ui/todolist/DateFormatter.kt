package tk.vhhg.todoyandex.ui.todolist

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Singleton for re-using the date formatter
 */
object DateFormatter {
    private const val DATE_FORMAT = "dd MMMM yyyy"
    val sdf: SimpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale("ru"))
}