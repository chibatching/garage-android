package com.sys1yagi.android.garage.sample.api

import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.io.IOException

class GarageClientTest {

    //mock server

    //not auth yet
    @Test
    fun notYetAuth() {
        val client: GarageClient = GarageClient(GarageConfiguration.make {
            port = 3000
            applicationId = "a"
            applicationSecret = "b"
            this.endpoint = "localhost"
            this.versionName = "v1"
            this.client = OkHttpClient()
        })
        try {
            val response = client.get(Path("users")).execute()
            assertThat(response.code()).isEqualTo(401)
        } catch(e: IOException) {
            fail(e.message)
        }
    }

    //have a token
    @Test
    fun haveAAuthToken() {

    }

    //request queue

}
