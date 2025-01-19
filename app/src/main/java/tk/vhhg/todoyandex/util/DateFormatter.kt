package tk.vhhg.todoyandex.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Singleton for re-using the date formatter
 */
object DateFormatter {
    private const val DATE_FORMAT = "dd MMMM yyyy"
    private val sdf: SimpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale("ru"))
    fun toString(date: Date?): String? = date?.let { sdf.format(it) }
}