package com.sys1yagi.android.garage

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.io.IOException

class GarageClientTest {

    @Test
    fun notYetAuth() {
        val mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setResponseCode(401))
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("{\"access_token\":\"4bf2014681df03d9fa6ff2469d7b5594d85de2a6ca7ab15bcc5fd33d07bd1139\",\"token_type\":\"bearer\",\"expires_in\":7200,\"scope\":\"public\"}"))
        mockWebServer.start()

        val client: GarageClient = GarageClient(GarageConfiguration.Companion.make {
            port = mockWebServer.port
            applicationId = "a"
            applicationSecret = "b"
            endpoint = mockWebServer.hostName
            client = OkHttpClient()
            authenticator = null
        })

        try {
            val response = client.get(Path("v1", "users")).execute()
            assertThat(response.code()).isEqualTo(401)
        } catch(e: IOException) {
            fail(e.message)
        }

        val req = mockWebServer.takeRequest()
        assertThat(req.method).isEqualTo("GET")
        assertThat(req.path).isEqualTo("/users")
        //        assertThat(mockWebServer.takeRequest(10, TimeUnit.MILLISECONDS)).isNotNull()
    }

    @Test
    fun haveAAuthToken() {

    }

    //request queue

}
