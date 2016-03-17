package com.sys1yagi.android.garage.sample.api

import android.os.Handler
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class GarageClient(val configuration: GarageConfiguration) {

    class CallbackDelegator(val handler: Handler?, val success: (Call, Response) -> Unit, val failed: (Call, IOException) -> Unit) : Callback {
        override fun onFailure(call: Call, exception: IOException) {
            handler?.let { it.post { failed(call, exception) } } ?: failed(call, exception)
        }

        override fun onResponse(call: Call, response: Response) {
            handler?.let { it.post { success(call, response) } } ?: success(call, response)
        }
    }

    fun get(path: String, success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit) {
        with(configuration) {
            val request = Request.Builder()
                    .url("${scheme}://${endpoint}:$port/${versionName}/$path")
                    .build()
            client.newCall(request)
                    .enqueue(CallbackDelegator(callbackHandler, success, failed))
        }

    }

}
