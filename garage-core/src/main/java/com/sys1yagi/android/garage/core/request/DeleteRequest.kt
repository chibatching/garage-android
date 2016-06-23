package com.sys1yagi.android.garage.core.request

import com.sys1yagi.android.garage.core.config.RequestConfiguration
import okhttp3.Call
import okhttp3.Request
import java.io.IOException

open class DeleteRequest(private val path: Path, private val config: RequestConfiguration, val requestPreparing: (Request.Builder) -> Request.Builder = { it }) : GarageRequest() {

    var parameter: Parameter? = null

    override fun url(): String {
        with(config) {
            val url =  "${scheme.value}://${endpoint}:${customPort ?: scheme.port}/${path.to()}" + (parameter?.let { "?${it.build()}" } ?: "")
            return url
        }
    }

    override fun newCall(requestProcessing: (Request.Builder) -> Request.Builder): Call {
        return config.client.newCall(
                requestProcessing(Request.Builder())
                        .url(url())
                        .delete()
                        .build())
    }

    override fun execute(success: (GarageResponse) -> Unit, failed: (GarageError) -> Unit) {
        val call = newCall(requestPreparing)
        try {
            val response = call.execute()
            success.invoke(GarageResponse(call, response))
        } catch(e: IOException) {
            failed.invoke(
                    GarageError(e).apply {
                        this.call = call
                    }
            )
        }
    }

    override fun requestTime() = System.currentTimeMillis()
}
