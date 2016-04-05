package com.sys1yagi.android.garage

import android.os.Handler
import com.google.gson.Gson
import com.sys1yagi.android.garage.auth.AccessTokenHandler
import com.sys1yagi.android.garage.auth.AccessTokenHolder
import com.sys1yagi.android.garage.auth.Authenticator
import com.sys1yagi.android.garage.auth.RequestErrorHandler
import com.sys1yagi.android.garage.core.BuildConfig
import com.sys1yagi.android.garage.impl.*
import okhttp3.OkHttpClient

class GarageConfiguration private constructor(val applicationId: String, val applicationSecret: String, val endpoint: String, val client: OkHttpClient) {

    var scheme = Scheme.HTTP
    var callbackHandler: Handler? = null
    var port = 0
    var headerProcessor: HeaderProcessor = DefaultHeaderProcessor()
    var authenticator: Authenticator = DefaultAuthenticator("anonymous")
    var accessTokenHandler: AccessTokenHandler = DefaultAccessTokenHandler(DefaultTimeProvider())
    var accessTokenHolder: AccessTokenHolder = OnMemoryAccessTokenHolder()
    var requestErrorHandler: RequestErrorHandler = DefaultRequestErrorHandler()
    var authEndpoint: String = endpoint
    var userAgent: String = "garage-android-${BuildConfig.VERSION_NAME}"
    var gson: Gson = Gson()

    companion object {
        fun make(
                applicationId: String,
                applicationSecret: String,
                endpoint: String,
                client: OkHttpClient,
                builder: GarageConfiguration.() -> Unit = {}): GarageConfiguration {
            return GarageConfiguration(applicationId, applicationSecret, endpoint, client).let {
                builder(it)
                it
            }
        }
    }

}
