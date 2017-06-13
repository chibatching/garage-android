package com.sys1yagi.android.garage.core

import com.sys1yagi.android.garage.core.auth.Authenticator
import com.sys1yagi.android.garage.core.config.GarageConfiguration
import com.sys1yagi.android.garage.core.request.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.util.*

open class GarageClient(val config: GarageConfiguration) {

    companion object {
        const val TAG = "garage-android"
        val MEDIA_TYPE_FORM_URLENCODED: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        val MEDIA_TYPE_JSON: MediaType = MediaType.parse("application/json; charset=utf-8");
        val MEDIA_TYPE_TEXT: MediaType = MediaType.parse("text/plain; charset=utf-8");

    }

    private val authenticators: ArrayList<Authenticator> = ArrayList()

    fun addAuthenticator(authenticator: Authenticator) {
        authenticators.add(authenticator)
    }

    open fun get(path: Path, parameter: Parameter? = null): Observable<Response> {
        return Observable.create { emitter ->
            val request = createGetRequest(path, parameter, emitter)
            requestOrAuth(request)
        }
    }

    open fun post(path: Path, body: RequestBody): Observable<Response> {
        return Observable.create { emitter ->
            val request = createPostRequest(path, body, emitter)
            requestOrAuth(request)
        }
    }

    open fun head(): Observable<Response> {
        return Observable.create {

        }
    }

    open fun put(path: Path, body: RequestBody): Observable<Response> {
        return Observable.create { emitter ->
            val request = createPutRequest(path, body, emitter)
            requestOrAuth(request)
        }
    }

    open fun patch(): Observable<Response> {
        return Observable.create {

        }
    }

    open fun delete(path: Path, parameter: Parameter? = null): Observable<Response> {
        return Observable.create { emitter ->
            val request = createDeleteRequest(path, parameter, emitter)
            requestOrAuth(request)
        }
    }

    private fun requestOrAuth(request: GarageRequest) {
        extractAuthRequestOnBeforeRequest(request)?.let { authRequest ->
            config.executorConfiguration.executor.enqueue(authRequest)
        } ?: config.executorConfiguration.executor.enqueue(request)
    }

    private fun prepare(): (Request.Builder) -> Request.Builder {
        return { builder ->
            authenticators.forEach {
                it.requestPreparing(builder)
            }
            builder
        }
    }

    private fun createGetRequest(path: Path, parameter: Parameter?, emitter: ObservableEmitter<in Response>) =
            GetRequest(path, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            emitter.onNext(garageResponse.response)
                            emitter.onComplete()
                        },
                        { error ->
                            emitter.onError(error)
                        }
                )
            }

    private fun createPostRequest(path: Path, body: RequestBody, emitter: ObservableEmitter<in Response>) =
            PostRequest(path, body, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            emitter.onNext(garageResponse.response)
                            emitter.onComplete()
                        },
                        { error ->
                            emitter.onError(error)
                        }
                )
            }

    private fun createPutRequest(path: Path, body: RequestBody, emitter: ObservableEmitter<in Response>) =
            PutRequest(path, body, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            emitter.onNext(garageResponse.response)
                            emitter.onComplete()
                        },
                        { error ->
                            emitter.onError(error)
                        }
                )
            }
    private fun createDeleteRequest(path: Path, parameter: Parameter?, emitter: ObservableEmitter<in Response>) =
            DeleteRequest(path, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            emitter.onNext(garageResponse.response)
                            emitter.onComplete()
                        },
                        { error ->
                            emitter.onError(error)
                        }
                )
            }

    private fun extractAuthRequestOnReceiveResponse(request: GarageRequest, response: GarageResponse): GarageRequest? {
        authenticators.forEach {
            if (it.shouldAuthentication(response)) {
                return it.createAuthRequest(
                        {
                            requestOrAuth(request)
                        },
                        { error ->
                            request.invoker?.callbackFailed?.invoke(error)
                        },
                        prepare()
                )
            }
        }
        return null
    }

    private fun extractAuthRequestOnBeforeRequest(request: GarageRequest): GarageRequest? {
        authenticators.forEach {
            if (it.shouldAuthentication(request)) {
                return it.createAuthRequest(
                        {
                            requestOrAuth(request)
                        },
                        { error ->
                            request.invoker?.callbackFailed?.invoke(error)
                        },
                        prepare()
                )
            }
        }
        return null
    }
}
