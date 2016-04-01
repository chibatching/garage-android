package com.sys1yagi.android.garage

import android.os.Handler
import com.google.gson.Gson
import com.sys1yagi.android.garage.auth.AccessTokenHolder
import com.sys1yagi.android.garage.auth.Authenticator
import com.sys1yagi.android.garage.impl.DefaultAuthenticator
import com.sys1yagi.android.garage.impl.OnMemoryAccessTokenHolder
import okhttp3.OkHttpClient

class GarageConfiguration private constructor(val applicationId: String, val applicationSecret: String, val endpoint: String, val client: OkHttpClient) {

    var scheme = Scheme.HTTP
    var callbackHandler: Handler? = null
    var port = 0
    var authenticator: Authenticator? = null
    var gson: Gson = Gson()
    var accessTokenHolder: AccessTokenHolder = OnMemoryAccessTokenHolder()
    var authEndpoint: String = endpoint

    companion object {
        fun make(
                applicationId: String,
                applicationSecret: String,
                endpoint: String,
                client: OkHttpClient,
                builder: GarageConfiguration.() -> Unit = {}): GarageConfiguration {
            return GarageConfiguration(applicationId, applicationSecret, endpoint, client).let {
                builder(it)
                verify(it)
            }
        }

        fun verify(instance: GarageConfiguration): GarageConfiguration {
            // Access the fields of lateinit . It happen crash If isn't initialized
            instance.applicationId
            instance.applicationSecret
            instance.endpoint
            instance.client
            instance.authenticator = instance.authenticator?.let { it } ?: DefaultAuthenticator("anonymous")
            return instance
        }
    }

}
