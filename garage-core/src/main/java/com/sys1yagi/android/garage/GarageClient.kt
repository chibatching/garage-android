package com.sys1yagi.android.garage

import okhttp3.*
import rx.Observable
import java.io.IOException
import java.net.HttpURLConnection

open class GarageClient(val configuration: GarageConfiguration) {

    class CallbackDelegator(val caller: Caller, val garaceClient: GarageClient, val success: (Call, Response) -> Unit, val failed: (Call, IOException) -> Unit, val maxRetryCount: Int = 0) : Callback {
        var retryCount = -1

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
            if (retryCount >= maxRetryCount) {
                return false
            }
            retryCount++
            with(garaceClient.configuration) {
                return authenticator?.let {
                    it.authenticate(garaceClient,
                            { call, response ->
                                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && doAuthenticate()) {
                                    return@authenticate
                                }
                                caller.enqueue(success, failed)
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

    class Caller(val call: Request, val garaceClient: GarageClient) {
        fun enqueue(success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit) {
            with(garaceClient.configuration) {
                client.newCall(call).enqueue(CallbackDelegator(this@Caller, garaceClient, success, failed))
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

    fun get(path: Path, headerProcessor: (Request.Builder) -> Request.Builder = { it }): Caller {
        with(configuration) {
            val request = headerProcessor(Request.Builder())
                    .url("${scheme}://${endpoint}:$port/${path.to()}")
                    .build()
            return Caller(request, this@GarageClient)
        }
    }

    fun post(path: Path, body: RequestBody, headerProcessor: (Request.Builder) -> Request.Builder = { it }): Caller {
        with(configuration) {
            val request = headerProcessor(Request.Builder())
                    .url("${scheme}://${endpoint}:$port/${path.to()}")
                    .post(body)
                    .build()
            return Caller(request, this@GarageClient)
        }
    }
}
