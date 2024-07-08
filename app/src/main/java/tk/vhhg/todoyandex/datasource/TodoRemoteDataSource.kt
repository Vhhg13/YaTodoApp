package tk.vhhg.todoyandex.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.model.ElementDTO
import tk.vhhg.todoyandex.model.ListDTO
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import java.util.UUID

/**
 * Implementation of [ITodoRemoteDataSource]
 */
class TodoRemoteDataSource(
    private val httpClient: HttpClient,
    private val lastRevision: SharedPreferences
) : ITodoRemoteDataSource {

    companion object {
        const val DELAY_BETWEEN_RETRIES = 2000L
        const val MAX_TRIES = 3
    }

    /**
     * keeps retrying the request while it results in a 5xx code
     * or until the response status code is anything but a 5xx code
     */
    private suspend fun tryRequest(
        maxTries: Int,
        request: suspend () -> HttpResponse
    ): HttpResponse {
        val triesAmount = maxTries.coerceAtLeast(1)
        repeat(triesAmount - 1) {
            val response = request()
            if (response.status.value < 500) return response
            delay(DELAY_BETWEEN_RETRIES)
        }
        val response = request()
        return response
    }

    override suspend fun getList(): Result<List<TodoItem>> {
        val response = tryRequest(maxTries = MAX_TRIES) {
            httpClient.get("list")
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // GET /list can only return 200 or 5xx
        if (response.status.isSuccess()) {
            val body = response.body<ListDTO>()
            lastRevision.setLastRevision(body.revision)
            return Result.Success(body.list) // 200
        }
        return Result.ServerError // 5xx
    }

    override suspend fun patchList(lst: List<TodoItem>): Result<List<TodoItem>> {
        val response = tryRequest(maxTries = MAX_TRIES) {
            httpClient.patch("list") {
                setRevisionHeader()
                setBody(ListDTO(list = lst, revision = lastRevision.getLastRevision()))
            }
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // PATCH /list can only return 200 or 5xx
        if (response.status.isSuccess()) {
            val body = response.body<ListDTO>()
            lastRevision.setLastRevision(body.revision)
            return Result.Success(body.list) // 200
        }
        return Result.ServerError //5xx
    }

    override suspend fun postToList(item: TodoItem): Result<TodoItem> {
        val response = tryRequest(maxTries = MAX_TRIES) {
            httpClient.post("list") {
                setRevisionHeader()
                setBody(ElementDTO(element = item, revision = lastRevision.getLastRevision()))
            }
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // POST /list can return 200, 400, or 5xx
        return when (response.status.value) {
            200 -> {
                val body = response.body<ElementDTO>()
                lastRevision.setLastRevision(body.revision)
                Result.Success(body.element)
            }

            400 -> Result.RevisionsDoNotMatch(lastRevision.getLastRevision())
            else -> Result.ServerError
        }
    }

    override suspend fun getById(id: UUID): Result<TodoItem> {
        val response = tryRequest(maxTries = MAX_TRIES) {
            httpClient.get("list/$id")
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // GET /list/{id} cen return 200, 404 or 5xx
        return when (response.status.value) {
            200 -> {
                val body = response.body<ElementDTO>()
                lastRevision.setLastRevision(body.revision)
                Result.Success(body.element)
            }

            404 -> Result.NotFound
            else -> Result.ServerError
        }
    }

    override suspend fun putById(item: TodoItem): Result<TodoItem> {
        val response = tryRequest(maxTries = MAX_TRIES) {
            httpClient.put("list/${item.id}") {
                setRevisionHeader()
                setBody(ElementDTO(element = item))
            }
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // PUT /list/{id} can return 200, 400, 404, 5xx
        return when (response.status.value) {
            200 -> {
                val body = response.body<ElementDTO>()
                lastRevision.setLastRevision(body.revision)
                Result.Success(body.element)
            }

            400 -> Result.RevisionsDoNotMatch(lastRevision.getLastRevision())
            404 -> Result.NotFound
            else -> Result.ServerError
        }
    }

    override suspend fun deleteById(id: String): Result<TodoItem> {
        val response = tryRequest(maxTries = MAX_TRIES) {
            httpClient.delete("list/$id") {
                setRevisionHeader()
            }
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // DELETE /list/{id} can return 200, 400, 404 or 5xx
        return when (response.status.value) {
            200 -> {
                val body = response.body<ElementDTO>()
                lastRevision.setLastRevision(body.revision)
                Result.Success(body.element)
            }

            400 -> Result.RevisionsDoNotMatch(lastRevision.getLastRevision())
            404 -> Result.NotFound
            else -> Result.ServerError
        }
    }

    // Util functions:
    private inline fun HttpResponse.ifUnauthorized(block: () -> Unit) {
        if (status.value == 401) block()
    }

    private fun SharedPreferences.getLastRevision(): Int {
        return getInt(App.KEY_LAST_REVISION, 0)
    }

    private fun SharedPreferences.setLastRevision(value: Int?) {
        edit {
            putInt(App.KEY_LAST_REVISION, value ?: getLastRevision())
        }
    }

    private fun HttpRequestBuilder.setRevisionHeader() {
        header("X-Last-Known-Revision", lastRevision.getLastRevision())
    }
}