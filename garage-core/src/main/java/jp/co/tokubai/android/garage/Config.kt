package jp.co.tokubai.android.garage

import android.text.TextUtils
import okhttp3.OkHttpClient

data class Config(val client: OkHttpClient, val endpoint: String) {
    var scheme: Scheme = Scheme.HTTP
    var customPort: Int? = null
    var isDebugMode: Boolean = false

    init {
        if (TextUtils.isEmpty(endpoint)) {
            throw IllegalArgumentException("endpoint is empty")
        }
    }
}
