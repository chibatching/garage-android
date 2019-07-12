package jp.co.tokubai.android.garage

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response

abstract class GarageRequest {
    abstract fun url(): String
    abstract fun newCall(requestProcessing: (Request.Builder) -> Request.Builder): Call
    abstract suspend fun execute(): Response
    abstract fun requestTime(): Long
}
