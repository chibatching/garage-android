package com.sys1yagi.android.garage.core.config

import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RequestConfigurationTest {

    @Test
    fun invalid() {
        try {
            RequestConfiguration(OkHttpClient(), "")
        } catch(e: IllegalArgumentException) {
            return
        }
        Assert.fail()
    }

    @Test
    fun valid() {
        RequestConfiguration(OkHttpClient(), "endpoint.com")
    }
}
