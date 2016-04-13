package com.sys1yagi.android.garage.core.config

import android.text.TextUtils
import com.sys1yagi.android.garage.core.request.Scheme
import okhttp3.OkHttpClient


open class RequestConfiguration(val client: OkHttpClient, val endpoint: String) {

    var scheme: Scheme = Scheme.HTTP
    var customPort: Int? = null

    init {
        if (TextUtils.isEmpty(endpoint)) {
            throw IllegalArgumentException("endpoint is empty")
        }
    }
}
