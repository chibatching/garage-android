package com.sys1yagi.android.garage

import android.os.Handler
import com.google.gson.Gson
import com.sys1yagi.android.garage.auth.AccessTokenHolder
import com.sys1yagi.android.garage.auth.Authenticator
import com.sys1yagi.android.garage.impl.DefaultAuthenticator
import com.sys1yagi.android.garage.impl.OnMemoryAccessTokenHolder
import okhttp3.OkHttpClient


class GarageConfiguration() {

    var scheme = "http"
    var callbackHandler: Handler? = null
    var port = 80
    var authenticator: Authenticator? = null
    var gson: Gson? = null
    var accessTokenHolder: AccessTokenHolder = OnMemoryAccessTokenHolder()
    lateinit var applicationId: String
    lateinit var applicationSecret: String
    lateinit var endpoint: String
    lateinit var client: OkHttpClient

    companion object {
        fun make(builder: GarageConfiguration.() -> Unit): GarageConfiguration {
            return GarageConfiguration().let {
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
            instance.gson = instance.gson?.let { it } ?: Gson()
            return instance
        }
    }

}
