package jp.co.tokubai.android.garage

import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class PostRequest(
    private val path: Path,
    private val requestBody: RequestBody,
    private val config: Config,
    val requestPreparing: (Request.Builder) -> Request.Builder = { it }
) : GarageRequest() {

    var parameter: Parameter? = null

    override fun url(): String {
        with(config) {
            return ("${scheme.value}://${endpoint}:${customPort
                ?: scheme.port}/${path.to()}" + (parameter?.let { "?${it.build()}" }
                ?: "")).apply {
                if (config.isDebugMode) {
                    Log.d(GarageClient.TAG, "POST:$this")
                }
            }
        }
    }

    override fun newCall(requestProcessing: (Request.Builder) -> Request.Builder): Call {
        return config.client.newCall(
            requestProcessing(Request.Builder())
                .url(url())
                .post(requestBody)
                .build()
        )
    }

    override suspend fun execute(): Response = suspendCancellableCoroutine { continuation ->
        val call = newCall(requestPreparing)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive && !call.isCanceled()) {
                    continuation.resumeWithException(GarageError(e).apply {
                        this.call = call
                        if (config.isDebugMode) {
                            e.printStackTrace()
                        }
                    })
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (config.isDebugMode) {
                    Log.d(GarageClient.TAG, "POST ${response.code} ${response.request.url}")
                }
                continuation.resume(response)
            }
        })
        continuation.invokeOnCancellation {
            call.cancel()
        }
    }

    override fun requestTime() = System.currentTimeMillis()
}
