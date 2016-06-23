package com.sys1yagi.android.garage.core.request

import okhttp3.Call
import okhttp3.Response

open class GarageError(e: Throwable?) : Exception(e) {
    var call: Call? = null
    var response: Response? = null
}
