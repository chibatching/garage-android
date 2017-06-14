package jp.co.tokubai.android.garage

import com.sys1yagi.android.garage.core.request.Parameter
import com.sys1yagi.android.garage.core.request.Path
import io.reactivex.Observable
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

open class GarageClient2(val config: Config) {
    val authenticators = arrayListOf<Authenticator>()

    fun addAuthenticator(authenticator: Authenticator) {
        authenticators.add(authenticator)
    }

    private fun prepare(): (Request.Builder) -> Request.Builder {
        return { builder ->
            authenticators.forEach {
                it.requestPreparing(builder)
            }
            builder
        }
    }

    fun get(path: Path, parameter: Parameter? = null): Response {
        val request = GetRequest(path, config, prepare())
        authenticators.forEach {
            it.authenticationIfNeeded(request)
        }
        val response = request.execute()
        return response
    }

    open fun post(path: Path, body: RequestBody): Response {
        val request = PostRequest(path, body, config, prepare())
        authenticators.forEach {
            it.authenticationIfNeeded(request)
        }
        val response = request.execute()
        return response
    }

    open fun head(): Observable<Response> {
        return Observable.create {

        }
    }

    open fun put(path: Path, body: RequestBody): Response {
        TODO()
    }

    open fun patch(): Response {
        TODO()
    }

    open fun delete(path: Path, parameter: Parameter? = null): Response {
        TODO()
    }
}