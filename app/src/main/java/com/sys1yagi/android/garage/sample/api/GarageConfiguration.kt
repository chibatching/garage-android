package com.sys1yagi.android.garage.sample.api

import android.os.Handler
import okhttp3.OkHttpClient


class GarageConfiguration() {
    var scheme = "http"
    var callbackHandler: Handler? = null
    var port = 80
    lateinit var endpoint: String
    lateinit var versionName: String
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
            instance.endpoint
            instance.versionName
            instance.client
            return instance
        }
    }

}
