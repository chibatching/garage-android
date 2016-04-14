package com.sys1yagi.android.garage.core

import com.sys1yagi.android.garage.core.auth.Authenticator
import com.sys1yagi.android.garage.core.config.GarageConfiguration
import com.sys1yagi.android.garage.core.request.*
import okhttp3.Request
import okhttp3.Response
import rx.Observable
import rx.Subscriber
import java.util.*

class GarageClient(val config: GarageConfiguration) {
    private val authenticators: ArrayList<Authenticator> = ArrayList()

    fun addAuthenticator(authenticator: Authenticator) {
        authenticators.add(authenticator)
    }

    fun get(path: Path, parameter: Parameter? = null): Observable<Response> {
        return Observable.create { subscriber ->
            val request = createRequest(path, parameter, subscriber)
            extractAuthRequest(request)?.let { authRequest ->
                config.executorConfiguration.executor.enqueue(authRequest)
            } ?: config.executorConfiguration.executor.enqueue(request)
        }
    }

    private fun prepare(): (Request.Builder) -> Request.Builder {
        return { builder ->
            authenticators.forEach {
                it.requestPreparing(builder)
            }
            builder
        }
    }

    private fun createRequest(path: Path, parameter: Parameter?, subscriber: Subscriber<in Response>) =
            GetRequest(path, config.requestConfiguration, prepare()).apply {
                this.parameter = parameter
                this.invoker = GarageRequest.Invoker(
                        { garageResponse ->
                            extractAuthRequest(this, garageResponse)?.let { authRequest ->
                                config.executorConfiguration.executor.enqueue(authRequest)
                                return@Invoker
                            }
                            subscriber.onNext(garageResponse.response)
                            subscriber.onCompleted()
                        },
                        {
                            subscriber.onError(it.exception)
                        }
                )
            }

    fun post(): Observable<Response> {
        return Observable.create {
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

    fun delete(): Observable<Response> {
        return Observable.create {

        }
    }

    private fun extractAuthRequest(request: GetRequest, response: GarageResponse): GarageRequest? {
        authenticators.forEach {
            if (it.shouldAuthentication(response)) {
                return it.createAuthRequest(
                        {
                            config.executorConfiguration.executor.enqueue(request)
                        },
                        { error ->
                            request.invoker?.callbackFailed?.invoke(error)
                        },
                        {it}
                )
            }
        }
        return null
    }

    private fun extractAuthRequest(request: GetRequest): GarageRequest? {
        authenticators.forEach {
            if (it.shouldAuthentication(request)) {
                return it.createAuthRequest(
                        {
                            config.executorConfiguration.executor.enqueue(request)
                        },
                        { error ->
                            request.invoker?.callbackFailed?.invoke(error)
                        },
                        {it}
                )
            }
        }
        return null
    }
}
