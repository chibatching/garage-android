package com.sys1yagi.android.garage

import android.text.TextUtils
import okhttp3.*
import rx.Observable
import java.io.IOException
import java.net.HttpURLConnection

open class GarageClient(val configuration: GarageConfiguration) {

    companion object {
        class CallbackDelegator(val caller: Caller, val garaceClient: GarageClient, val success: (Call, Response) -> Unit, val failed: (Call, IOException) -> Unit, val maxRetryCount: Int) : Callback {
            var retryCount = 0

            override fun onFailure(call: Call, exception: IOException) {
                with(garaceClient.configuration) {
                    callbackHandler?.let { it.post { failed(call, exception) } } ?: failed(call, exception)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                with(garaceClient.configuration) {
                    if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && doAuthenticate()) {
                        return
                    }
                    callbackHandler?.let { it.post { success(call, response) } } ?: success(call, response)
                }
            }

            fun doAuthenticate(isAuthRequest: Boolean = false): Boolean {
                System.out.println("retry:$retryCount, $maxRetryCount")
                if (retryCount >= maxRetryCount) {
                    return false
                }
                retryCount += 1
                with(garaceClient.configuration) {
                    return authenticator.let {
                        it.authenticate(garaceClient,
                                { call, response ->
                                    if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && doAuthenticate()) {
                                        return@authenticate
                                    }
                                    caller.enqueue(success, failed, this@CallbackDelegator)
                                },
                                { call, exception ->
                                    with(garaceClient.configuration) {
                                        callbackHandler?.let { it.post { failed(call, exception) } } ?: failed(call, exception)
                                    }
                                })
                        true
                    } ?: false
                }
            }
        }

        class Caller(val request: Request.Builder, val garageClient: GarageClient) {
            private var maxRetryCount = 1

            fun setMaxRetryCount(count: Int): Caller {
                maxRetryCount = count
                return this
            }

            fun enqueue(success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit, callback: CallbackDelegator? = null, isAuthRequest: Boolean = false) {
                with(garageClient.configuration) {
                    if (!isAuthRequest and accessTokenHandler.shouldAuthentication(accessTokenHolder)) {
                        if (callback != null) {
                            callback.doAuthenticate()
                        } else {
                            val result = CallbackDelegator(this@Caller, garageClient, success, failed, maxRetryCount).doAuthenticate()
                        }
                    } else {
                        client.newCall(garageHeader(this, request).build()).enqueue(
                                callback?.let { it }
                                        ?: CallbackDelegator(this@Caller, garageClient, success, failed, maxRetryCount))
                    }
                }
            }

            fun execute(): Response {
                return Observable.create<Response> { subscriber ->
                    enqueue(
                            { c, r ->
                                subscriber.onNext(r)
                                subscriber.onCompleted()
                            },
                            { c, e ->
                                subscriber.onError(e)
                            }
                    )
                }.toBlocking().first()
            }
        }

        fun garageHeader(configuration: GarageConfiguration, builder: Request.Builder): Request.Builder {
            builder.header("User-Agent", configuration.userAgent)
            if (!TextUtils.isEmpty(configuration.accessTokenHolder.accessToken)) {
                builder.header("Authorization", "Bearer ${configuration.accessTokenHolder.accessToken}")
            }

            return builder
        }
    }

    fun get(path: Path, parameter: Parameter? = null, customHeaderProcessor: (Request.Builder) -> Unit = { }): Caller {
        with(configuration) {
            val request =
                    Request.Builder()
                            .apply {
                                headerProcessor.invoke(this)
                                customHeaderProcessor.invoke(this)
                                val url = "${scheme.string}://${endpoint}:${if (port == 0) scheme.defaultPort else port}/${path.to()}" + (parameter?.let { "?${it.build()}" } ?: "")
                                url(url)
                            }.get()
            return Caller(request, this@GarageClient)
        }
    }

    fun post(path: Path, body: RequestBody, headerProcessor: (Request.Builder) -> Unit = { }): Caller = postWithEndpoint(configuration.endpoint, path, body, headerProcessor)

    fun postWithEndpoint(endpoint: String, path: Path, body: RequestBody, customHeaderProcessor: (Request.Builder) -> Unit = { }): Caller {
        with(configuration) {
            val request =
                    Request.Builder().apply {
                        headerProcessor.invoke(this)
                        customHeaderProcessor.invoke(this)
                    }
                            .url("${scheme.string}://$endpoint:${if (port == 0) scheme.defaultPort else port}/${path.to()}")
                            .post(body)
            return Caller(request, this@GarageClient)
        }
    }
}
