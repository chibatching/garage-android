package com.sys1yagi.android.garage.core

import com.sys1yagi.android.garage.core.auth.Authenticator
import com.sys1yagi.android.garage.core.config.GarageConfiguration
import com.sys1yagi.android.garage.core.request.*
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import rx.Observable
import rx.Subscriber
import java.util.*

class GarageClient(val config: GarageConfiguration) {

    companion object {
        val MEDIA_TYPE_FORM_URLENCODED: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        val MEDIA_TYPE_JSON: MediaType = MediaType.parse("application/json; charset=utf-8");
    }

    private val authenticators: ArrayList<Authenticator> = ArrayList()

    fun addAuthenticator(authenticator: Authenticator) {
        authenticators.add(authenticator)
    }

    fun get(path: Path, parameter: Parameter? = null): Observable<Response> {
        return Observable.create { subscriber ->
            val request = createGetRequest(path, parameter, subscriber)
            requestOrAuth(request)
        }
    }

    fun post(path: Path, body: RequestBody): Observable<Response> {
        return Observable.create { subscriber ->
            val request = createPostRequest(path, body, subscriber)
            requestOrAuth(request)
        }
    }


    fun head(): Observable<Response> {
        return Observable.create {

        }
    }

    fun put(): Observable<Response> {
        return Observable.create {

        }
    }

    fun patch(): Observable<Response> {
        return Observable.create {

        }
    }

    fun delete(path: Path, parameter: Parameter? = null): Observable<Response> {
        return Observable.create { subscriber ->
            val request = createDeleteRequest(path, parameter, subscriber)
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

    private fun createGetRequest(path: Path, parameter: Parameter?, subscriber: Subscriber<in Response>) =
            GetRequest(path, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            subscriber.onNext(garageResponse.response)
                            subscriber.onCompleted()
                        },
                        { error ->
                            subscriber.onError(error)
                        }
                )
            }

    private fun createPostRequest(path: Path, body: RequestBody, subscriber: Subscriber<in Response>) =
            PostRequest(path, body, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            subscriber.onNext(garageResponse.response)
                            subscriber.onCompleted()
                        },
                        { error ->
                            subscriber.onError(error)
                        }
                )
            }

    private fun createDeleteRequest(path: Path, parameter: Parameter?, subscriber: Subscriber<in Response>) =
            DeleteRequest(path, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequestOnReceiveResponse(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            subscriber.onNext(garageResponse.response)
                            subscriber.onCompleted()
                        },
                        { error ->
                            subscriber.onError(error)
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
