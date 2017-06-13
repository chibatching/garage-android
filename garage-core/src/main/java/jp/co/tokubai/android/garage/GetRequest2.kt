package jp.co.tokubai.android.garage

import android.util.Log
import com.sys1yagi.android.garage.core.GarageClient
import com.sys1yagi.android.garage.core.request.GarageError
import com.sys1yagi.android.garage.core.request.GarageResponse
import com.sys1yagi.android.garage.core.request.Parameter
import com.sys1yagi.android.garage.core.request.Path
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response

class GetRequest2(private val path: Path, private val config: Config, val requestPreparing: (Request.Builder) -> Request.Builder = { it }) {
    var parameter: Parameter? = null

    fun url(): String {
        with(config) {
            return ("${scheme.value}://$endpoint:${customPort ?: scheme.port}/${path.to()}" + (parameter?.let { "?${it.build()}" } ?: "")).apply {
                if (config.isDebugMode) {
                    Log.d(GarageClient.TAG, "GET:$this")
                }
            }
        }
    }

    fun newCall(requestProcessing: (Request.Builder) -> Request.Builder): Call {
        return config.client.newCall(
                requestProcessing(Request.Builder())
                        .url(url())
                        .build())
    }

    fun execute(): Response {
        val call = newCall(requestPreparing)
        try {
            return call.execute()
        } catch(e: Exception) {
            throw GarageError(e).apply {
                this.call = call
                if (config.isDebugMode) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun requestTime() = System.currentTimeMillis()
}