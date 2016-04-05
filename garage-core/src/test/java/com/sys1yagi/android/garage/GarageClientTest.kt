package com.sys1yagi.android.garage

import com.sys1yagi.android.garage.testtool.milliseconds
import com.sys1yagi.android.garage.testtool.takeRequest
import com.sys1yagi.kmockito.any
import com.sys1yagi.kmockito.invoked
import com.sys1yagi.kmockito.mock
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class GarageClientTest {

    fun createGarageClient(mockWebServer: MockWebServer, builder: GarageConfiguration.() -> Unit = {}) =
            GarageClient(GarageConfiguration.Companion.make("a", "b", mockWebServer.hostName, OkHttpClient()) {
                port = mockWebServer.port
                builder.invoke(this)
            })

    lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun notYetAuth() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("{\"access_token\":\"4bf2014681df03d9fa6ff2469d7b5594d85de2a6ca7ab15bcc5fd33d07bd1139\",\"token_type\":\"bearer\",\"expires_in\":7200,\"scope\":\"public\"}"))
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.start()

        val client: GarageClient = createGarageClient(mockWebServer)

        try {
            val response = client.get(Path("v1", "users")).execute()
            assertThat(response.code()).isEqualTo(200)
        } catch(e: IOException) {
            fail(e.message)
        }

        mockWebServer.takeRequest().let {
            assertThat(it.method).isEqualTo("POST")
            assertThat(it.path).isEqualTo("/oauth/token")
        }
        mockWebServer.takeRequest(10.milliseconds).let {
            assertThat(it.method).isEqualTo("GET")
            assertThat(it.path).isEqualTo("/v1/users")
        }
        assertThat(client.configuration.accessTokenHolder.accessToken).isEqualTo("4bf2014681df03d9fa6ff2469d7b5594d85de2a6ca7ab15bcc5fd33d07bd1139")
    }

    @Test
    fun hasAccessToken() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.start()
        val client: GarageClient = createGarageClient(mockWebServer, {
            accessTokenHandler = mock()
            accessTokenHandler.shouldAuthentication(any()).invoked.thenReturn(false)
        })
        client.get(Path("v1", "test")).execute()
        mockWebServer.takeRequest().let {
            assertThat(it.method).isEqualTo("GET")
            assertThat(it.path).isEqualTo("/v1/test")
        }
    }

    @Test
    fun customUserAgent() {
        val mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.start()
        val client: GarageClient = createGarageClient(mockWebServer, {
            userAgent = "custom"
            accessTokenHandler = mock()
            accessTokenHandler.shouldAuthentication(any()).invoked.thenReturn(false)
        })

        client.get(Path("v1", "test")).execute()
        mockWebServer.takeRequest().let {
            assertThat(it.getHeader("User-Agent")).isEqualTo("custom")
            assertThat(it.method).isEqualTo("GET")
            assertThat(it.path).isEqualTo("/v1/test")
        }
    }

    @Test
    fun accessTokenExpired() {
        val mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("{\"access_token\":\"new token\",\"token_type\":\"bearer\",\"expires_in\":7200,\"scope\":\"public\"}"))
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.start()
        val client: GarageClient = createGarageClient(mockWebServer)
        client.get(Path("v1", "test")).execute()
        assertThat(client.configuration.accessTokenHolder.accessToken)
                .isEqualTo("new token")
    }

    @Test
    fun customHeader() {

        class TestHeaderProcessor : HeaderProcessor {
            override fun invoke(builder: Request.Builder) {
                builder.header("test", "value")
            }
        }

        val mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        mockWebServer.start()
        val client: GarageClient = createGarageClient(mockWebServer, {
            headerProcessor = TestHeaderProcessor()
            accessTokenHandler = mock()
            accessTokenHandler.shouldAuthentication(any()).invoked.thenReturn(false)
        })
        client.get(Path("v1", "test")).execute()
        mockWebServer.takeRequest().let {
            assertThat(it.getHeader("test")).isEqualTo("value")
        }
    }


    //request queue

}
