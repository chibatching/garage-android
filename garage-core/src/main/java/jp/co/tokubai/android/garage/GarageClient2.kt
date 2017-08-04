package jp.co.tokubai.android.garage

import com.sys1yagi.android.garage.core.request.GarageError
import com.sys1yagi.android.garage.core.request.Parameter
import com.sys1yagi.android.garage.core.request.Path
import io.reactivex.Observable
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

typealias RequestBefore = (Request.Builder) -> Request.Builder

open class GarageClient2(val config: Config) {
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

    inline fun request(authRetryMaxCount: Int = 1, requestSet: () -> Pair<RequestBefore, GarageRequest>): Response {
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

    fun get(path: Path, parameter: Parameter? = null, authRetryMaxCount: Int = 1): Response {
        return request {
            val before = prepare()
            val request = GetRequest(path, config, before).apply {
                this.parameter = parameter
            }
            Pair(before, request)
        }
    }

    open fun post(path: Path, body: RequestBody, authRetryMaxCount: Int = 1): Response {
        return request {
            val before = prepare()
            val request = PostRequest(path, body, config, before)
            Pair(before, request)
        }
    }

    open fun head(): Observable<Response> {
        return Observable.create {

        }
    }

    open fun put(path: Path, body: RequestBody, authRetryMaxCount: Int = 1): Response {
        return request {
            val before = prepare()
            val request = PutRequest(path, body, config, before)
            Pair(before, request)
        }
    }

    open fun patch(): Response {
        TODO()
    }

    open fun delete(path: Path, parameter: Parameter? = null, authRetryMaxCount: Int = 1): Response {
        return request {
            val before = prepare()
            val request = DeleteRequest(path, config, before).apply {
                this.parameter = parameter
            }
            Pair(before, request)
        }
    }
}
