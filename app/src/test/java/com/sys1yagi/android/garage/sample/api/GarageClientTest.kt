package com.sys1yagi.android.garage.sample.api


import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.io.IOException
import java.util.concurrent.TimeUnit

class GarageClientTest {

    @Test
    fun notYetAuth() {
        val mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setResponseCode(401))
        mockWebServer.start()

        val client: GarageClient = GarageClient(GarageConfiguration.make {
            port = mockWebServer.port
            applicationId = "a"
            applicationSecret = "b"
            this.endpoint = mockWebServer.hostName
            this.client = OkHttpClient()
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
        assertThat(mockWebServer.takeRequest(10, TimeUnit.MILLISECONDS)).isNull()
    }

    @Test
    fun haveAAuthToken() {

    }

    //request queue

}
