package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.GarageConfiguration
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultAuthenticatorTest {

    @Test
    fun header() {
        val authenticator = DefaultAuthenticator("test")
        val config = GarageConfiguration.make("hoge", "moge", "c", OkHttpClient())

        val builder = authenticator
                .header(config).invoke(Request.Builder())
                .url("http://test")

        assertThat(builder.build().header("Authorization")).isEqualTo("Basic aG9nZTptb2dl")
    }
}
