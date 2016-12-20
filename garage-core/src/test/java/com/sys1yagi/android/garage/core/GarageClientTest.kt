package com.sys1yagi.android.garage.core

import com.google.gson.Gson
import com.sys1yagi.android.garage.core.auth.AccessTokenContainer
import com.sys1yagi.android.garage.core.config.*
import com.sys1yagi.android.garage.core.executor.Executor
import com.sys1yagi.android.garage.core.impl.DefaultAuthenticator
import com.sys1yagi.android.garage.core.impl.GsonConverter
import com.sys1yagi.android.garage.core.impl.OnMemoryAccessTokenContainer
import com.sys1yagi.android.garage.core.request.Path
import com.sys1yagi.android.garage.core.testtool.given
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import io.reactivex.subscribers.TestSubscriber
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GarageClientTest {

    lateinit var garageClient: GarageClient

    lateinit var accessTokenContainer: AccessTokenContainer

    lateinit var authenticator: DefaultAuthenticator

    lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        accessTokenContainer = OnMemoryAccessTokenContainer()
        mockWebServer = MockWebServer()
        val authConfig = AuthenticatorConfiguration(OkHttpClient(), mockWebServer.hostName, "id", "secret").apply {
            customPort = mockWebServer.port
        }
        authenticator = DefaultAuthenticator("test", authConfig, accessTokenContainer)

        val client = OkHttpClient()
        garageClient = GarageClient(GarageConfiguration(
                RequestConfiguration(client, mockWebServer.hostName).apply {
                    customPort = mockWebServer.port
                },
                ExecutorConfiguration(Executor()),
                JsonConvertConfiguration(GsonConverter(Gson()))
        )).apply {
            addAuthenticator(authenticator)
        }
    }

    @Test
    fun withAuthRequest() {
        given("Have not access token") {
            accessTokenContainer.accessToken = ""
            on("Request to API") {
                mockWebServer.enqueue(MockResponse().setResponseCode(200)
                        .setBody("{\"access_token\":\"access token\",\"token_type\":\"bearer\",\"expires_in\":7200,\"scope\":\"public\"}"))
                mockWebServer.enqueue(MockResponse().setResponseCode(200))

                val testSubscriber = garageClient.get(Path("v1", "users/10")).test()
                testSubscriber.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS)
                testSubscriber.assertNoErrors()

                it("authenticate at first") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/oauth/token")
                    }
                }
                it("request normal request with Authorization header") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/v1/users/10")
                        assertThat(it.getHeader("Authorization")).isEqualTo("Bearer access token")
                    }
                }
            }
        }
    }

    @Test
    fun receive401() {
        given("Have a access token") {
            accessTokenContainer.accessToken = "access token"
            accessTokenContainer.savedAt = System.currentTimeMillis()
            accessTokenContainer.expired = 1000000000L

            on("Request to API and receive 401") {
                mockWebServer.enqueue(MockResponse().setResponseCode(401))
                mockWebServer.enqueue(MockResponse().setResponseCode(200)
                        .setBody("{\"access_token\":\"token\",\"token_type\":\"bearer\",\"expires_in\":7200,\"scope\":\"public\"}"))
                mockWebServer.enqueue(MockResponse().setResponseCode(200))

                val testSubscriber = garageClient.get(Path("v1", "users/10")).test()
                testSubscriber.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS)
                testSubscriber.assertNoErrors()

                it("should receive 401") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/v1/users/10")
                    }
                }
                it("should do auth request") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/oauth/token")
                    }
                }
                it("should call source request") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/v1/users/10")
                    }
                }
            }
        }
    }

    @Test
    fun notRetryWhenReceive401() {
        given("Have a access token") {
            accessTokenContainer.accessToken = "access token"
            accessTokenContainer.savedAt = System.currentTimeMillis()
            accessTokenContainer.expired = 1000000000L

            on("Request to API and receive 401 x 2") {
                mockWebServer.enqueue(MockResponse().setResponseCode(401))
                mockWebServer.enqueue(MockResponse().setResponseCode(401))

                val testSubscriber = garageClient.get(Path("v1", "users/10")).test()
                testSubscriber.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS)

                it("should receive 401") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/v1/users/10")
                    }
                }
                it("should do auth request") {
                    mockWebServer.takeRequest(5, TimeUnit.MILLISECONDS).let {
                        assertThat(it.path).isEqualTo("/oauth/token")
                    }
                }
                it("should callback error to source request") {
                    testSubscriber.assertError(Throwable::class.java)
                }
            }
        }
    }

}
