package tk.vhhg.todoyandex.model

sealed interface Result<out T> {
    data class Success<T>(val value: T) : Result<T>
    data class Error(val e: Exception) : Result<Nothing>
}