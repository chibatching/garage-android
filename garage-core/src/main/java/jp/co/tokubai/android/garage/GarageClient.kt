package jp.co.tokubai.android.garage

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response

open class GarageClient(val config: Config) {

    companion object {
        const val TAG = "garage-android"
        val MEDIA_TYPE_FORM_URLENCODED: MediaType =
            MediaType.get("application/x-www-form-urlencoded; charset=utf-8")
        val MEDIA_TYPE_JSON: MediaType = MediaType.get("application/json; charset=utf-8")
        val MEDIA_TYPE_TEXT: MediaType = MediaType.get("text/plain; charset=utf-8")
    }

    val authenticators = arrayListOf<Authenticator>()

    fun addAuthenticator(authenticator: Authenticator) {
        authenticators.add(authenticator)
    }

    private fun prepare(): RequestBefore {
        return { builder ->
            authenticators.forEach {
                it.requestPreparing(builder)
            }
            builder
        }
    }

    suspend inline fun request(
        authRetryMaxCount: Int = 1,
        requestSet: () -> Pair<RequestBefore, GarageRequest>
    ): Response {
        var count = 0
        while (count <= authRetryMaxCount) {
            val (before, request) = requestSet()
            authenticators.forEach {
                it.authenticationIfNeeded(request, before)
            }
            val response = request.execute()

            if (authenticators.count {
                    it.authenticationIfNeeded(request, response)
                } > 0) {
                count++
            } else {
                return response
            }
        }
        throw GarageError(null)
    }

    open suspend fun get(
        path: Path,
        parameter: Parameter? = null,
        authRetryMaxCount: Int = 1
    ): Response {
        return request {
            val before = prepare()
            val request = GetRequest(path, config, before).apply {
                this.parameter = parameter
            }
            Pair(before, request)
        }
    }

    open suspend fun post(path: Path, body: RequestBody, authRetryMaxCount: Int = 1): Response {
        return request {
            val before = prepare()
            val request = PostRequest(path, body, config, before)
            Pair(before, request)
        }
    }

    open suspend fun head(): Response {
        TODO()
    }

    open suspend fun put(path: Path, body: RequestBody, authRetryMaxCount: Int = 1): Response {
        return request {
            val before = prepare()
            val request = PutRequest(path, body, config, before)
            Pair(before, request)
        }
    }

    open suspend fun patch(): Response {
        TODO()
    }

    open suspend fun delete(
        path: Path,
        parameter: Parameter? = null,
        authRetryMaxCount: Int = 1
    ): Response {
        return request {
            val before = prepare()
            val request = DeleteRequest(path, config, before).apply {
                this.parameter = parameter
            }
            Pair(before, request)
        }
    }
}
