package com.sys1yagi.android.garage.auth

import com.google.gson.Gson
import com.sys1yagi.android.garage.testtool.AssetsUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AuthResponseBodyTest {

    @Test
    fun fromJson() {
        val body: AuthResponseBody = Gson().fromJson(AssetsUtil.readString("auth_response_body"), AuthResponseBody::class.java)
        assertThat(body).isNotNull()

        assertThat(body.accessToken).isEqualTo("TOKEN")
        assertThat(body.tokenType).isEqualTo("bearer")
        assertThat(body.expiresIn).isEqualTo(7200)
        assertThat(body.scope).isEqualTo("public")
    }
}
