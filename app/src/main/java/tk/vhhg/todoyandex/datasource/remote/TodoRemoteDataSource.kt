package tk.vhhg.todoyandex.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import tk.vhhg.todoyandex.datasource.local.preferences.IRevisionLocalDataSource
import tk.vhhg.todoyandex.di.TodoAppScope
import tk.vhhg.todoyandex.model.ListDTO
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import javax.inject.Inject

/**
 * Implementation of [ITodoRemoteDataSource]
 */
@TodoAppScope
class TodoRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val revision: IRevisionLocalDataSource
) : ITodoRemoteDataSource {

    override suspend fun getList(): Result<ListDTO> {
        val response = httpClient.get("list")
        response.ifUnauthorized { return Result.Unauthorized }

        // GET /list can only return 200 or 5xx
        if (response.status.isSuccess()) {
            val body = response.body<ListDTO>()
            return Result.Success(body) // 200
        }
        return Result.ServerError // 5xx
    }

    override suspend fun patchList(lst: List<TodoItem>): Result<ListDTO> {
        val response = httpClient.patch("list") {
            setRevisionHeader()
            setBody(ListDTO(list = lst, revision = revision.get()))
        }
        response.ifUnauthorized { return Result.Unauthorized }

        // PATCH /list can only return 200 or 5xx
        if (response.status.isSuccess()) {
            val body = response.body<ListDTO>()
            return Result.Success(body) // 200
        }
        return Result.ServerError //5xx
    }

    // Util functions:
    private inline fun HttpResponse.ifUnauthorized(block: () -> Unit) {
        if (status.value == 401) block()
    }

    private fun HttpRequestBuilder.setRevisionHeader() {
        header("X-Last-Known-Revision", revision.get())
    }
}