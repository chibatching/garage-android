package com.sys1yagi.android.garage.core.request

import okhttp3.Call
import okhttp3.Request

abstract class GarageRequest {
    data class Invoker(val callbackSuccess: (GarageResponse) -> Unit, val callbackFailed: (GarageError) -> Unit)

    var invoker: Invoker? = null

    abstract fun url(): String
    abstract fun newCall(requestProcessing: (Request.Builder) -> Request.Builder): Call
    abstract fun execute(success: (GarageResponse) -> Unit, failed: (GarageError) -> Unit)
    abstract fun requestTime(): Long
}
