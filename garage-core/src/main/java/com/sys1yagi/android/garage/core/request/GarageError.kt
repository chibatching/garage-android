package com.sys1yagi.android.garage.core.request

import okhttp3.Call
import okhttp3.Response

open class GarageError : Exception() {
    var call: Call? = null
    var response: Response? = null
    var exception: Exception? = null
}
