package com.sys1yagi.android.garage.core.config

import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AuthenticationConfigurationTest {

    @Test
    fun valid() {
        AuthenticatorConfiguration(OkHttpClient(), "auth.test.com", "id", "secret")
    }
}
