package tk.vhhg.todoyandex.model

/**
 * Used to manage errors that can occur when dealing with data retrieval
 */
sealed interface Result<out T> {
    data class Success<T>(val value: T) : Result<T>
    data object ServerError: Result<Nothing>
    data class RevisionsDoNotMatch(val revision: Int): Result<Nothing>
    data object Unauthorized: Result<Nothing>
    data object NotFound: Result<Nothing>
    data class UnknownError(val err: Throwable): Result<Nothing>
    data object NoInternetConnection: Result<Nothing>
}