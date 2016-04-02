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

            fun doAuthenticate(): Boolean {
                System.out.println("retry:$retryCount, $maxRetryCount")
                if (retryCount >= maxRetryCount) {
                    return false
                }
                retryCount += 1
                with(garaceClient.configuration) {
                    return authenticator?.let {
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

            fun enqueue(success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit, callback: CallbackDelegator? = null) {
                with(garageClient.configuration) {
                    client.newCall(authorization(this, request).build()).enqueue(
                            callback?.let { it }
                                    ?: CallbackDelegator(this@Caller, garageClient, success, failed, maxRetryCount))
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

        fun authorization(configuration: GarageConfiguration, builder: Request.Builder): Request.Builder {
            if (TextUtils.isEmpty(configuration.accessTokenHolder.accessToken)) {
                return builder
            }
            return builder.header("Authorization", "Bearer ${configuration.accessTokenHolder.accessToken}")
        }
    }

    fun get(path: Path, parameter: Parameter? = null, headerProcessor: (Request.Builder) -> Request.Builder = { it }): Caller {
        with(configuration) {
            val request =
                    Request.Builder().apply {
                        headerProcessor(this)
                        val url = "${scheme.string}://${endpoint}:${if (port == 0) scheme.defaultPort else port}/${path.to()}" + (parameter?.let { "?${it.build()}" } ?: "")
                        url(url)
                        get()
                    }


            return Caller(request, this@GarageClient)
        }
    }

    fun post(path: Path, body: RequestBody, headerProcessor: (Request.Builder) -> Request.Builder = { it }): Caller = postWithEndpoint(configuration.endpoint, path, body, headerProcessor)

    fun postWithEndpoint(endpoint: String, path: Path, body: RequestBody, headerProcessor: (Request.Builder) -> Request.Builder = { it }): Caller {
        with(configuration) {
            val request = headerProcessor(Request.Builder())
                    .url("${scheme.string}://$endpoint:${if (port == 0) scheme.defaultPort else port}/${path.to()}")
                    .post(body)
            return Caller(request, this@GarageClient)
        }
    }
}
