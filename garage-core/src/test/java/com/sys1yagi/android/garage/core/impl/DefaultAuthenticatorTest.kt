package com.sys1yagi.android.garage.core.impl

import com.sys1yagi.android.garage.core.config.AuthenticatorConfiguration
import com.sys1yagi.android.garage.core.config.RequestConfiguration
import com.sys1yagi.android.garage.core.request.GarageResponse
import com.sys1yagi.android.garage.core.request.GetRequest
import com.sys1yagi.android.garage.core.request.Path
import com.sys1yagi.android.garage.core.testtool.given
import com.sys1yagi.kmockito.invoked
import com.sys1yagi.kmockito.mock
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class DefaultAuthenticatorTest {

    @RunWith(RobolectricTestRunner::class)
    @Config(manifest = Config.NONE)
    class ShouldAuthenticationTest {

        lateinit var accessTokenContainer: OnMemoryAccessTokenContainer

        lateinit var authenticator: DefaultAuthenticator

        @Before
        fun setUp() {
            accessTokenContainer = OnMemoryAccessTokenContainer()
            authenticator = DefaultAuthenticator("test", AuthenticatorConfiguration(OkHttpClient(), "auth.test.com", "id", "secret"), accessTokenContainer)
        }

        @Test
        fun shouldAuthenticationIsTrue() {
            val path = Path("v1", "users/10")
            val requestConfig = RequestConfiguration(OkHttpClient(), "test.com")
            val request = GetRequest(path, requestConfig)

            assertThat(authenticator.shouldAuthentication(request))
                    .isTrue()
        }

        @Test
        fun shouldAuthenticationIsTrue2() {
            val request: GetRequest = mock()

            request.requestTime().invoked.thenReturn(22)
            accessTokenContainer.accessToken = "test"
            accessTokenContainer.savedAt = 10
            accessTokenContainer.expired = 12

            assertThat(authenticator.shouldAuthentication(request))
                    .isTrue()
        }

        @Test
        fun shouldAuthenticationIsTrue3() {
            val request: GetRequest = mock()

            request.requestTime().invoked.thenReturn(22)
            accessTokenContainer.accessToken = ""
            accessTokenContainer.savedAt = 10
            accessTokenContainer.expired = 13

            assertThat(authenticator.shouldAuthentication(request))
                    .isTrue()
        }

        @Test
        fun shouldAuthenticationIsFalse() {
            val request: GetRequest = mock()

            request.requestTime().invoked.thenReturn(22)
            accessTokenContainer.accessToken = "test"
            accessTokenContainer.savedAt = 10
            accessTokenContainer.expired = 13

            assertThat(authenticator.shouldAuthentication(request))
                    .isFalse()
        }
    }

    @RunWith(RobolectricTestRunner::class)
    @Config(manifest = Config.NONE)
    class ShouldAuthenticationWhenReceiveResponse {
        lateinit var accessTokenContainer: OnMemoryAccessTokenContainer

        lateinit var authenticator: DefaultAuthenticator

        @Before
        fun setUp() {
            accessTokenContainer = OnMemoryAccessTokenContainer()
            authenticator = DefaultAuthenticator("test", AuthenticatorConfiguration(OkHttpClient(), "auth.test.com", "id", "secret"), accessTokenContainer)
        }

        @Test
        fun shouldAuthenticationTrue() {
            given("Receive 401") {
                accessTokenContainer.accessToken = ""
                on("authenticator.shouldAuthentication(response)") {
                    val request = Request.Builder()
                            .url("http://stub.com")
                            .build()
                    val response = Response.Builder()
                            .code(401)
                            .protocol(Protocol.HTTP_1_1)
                            .request(request)
                            .build()
                    val garageResponse: GarageResponse = GarageResponse(mock(), response)
                    it("return true") {
                        assertThat(authenticator.shouldAuthentication(garageResponse)).isTrue()
                    }
                }
            }
        }

        @Test
        fun shouldAuthenticationFalse() {
            given("Receive 200") {
                accessTokenContainer.accessToken = "token"
                on("authenticator.shouldAuthentication(response)") {
                    val request = Request.Builder()
                            .url("http://stub.com")
                            .build()
                    val response = Response.Builder()
                            .code(200)
                            .protocol(Protocol.HTTP_1_1)
                            .request(request)
                            .build()
                    val garageResponse: GarageResponse = GarageResponse(mock(), response)
                    it("return false") {
                        assertThat(authenticator.shouldAuthentication(garageResponse)).isFalse()
                    }
                }
            }
        }

    }

    @RunWith(RobolectricTestRunner::class)
    @Config(manifest = Config.NONE)
    class CreateAuthRequest {
        lateinit var accessTokenContainer: OnMemoryAccessTokenContainer

        lateinit var authenticator: DefaultAuthenticator

        lateinit var mockWebServer: MockWebServer

        @Before
        fun setUp() {
            accessTokenContainer = OnMemoryAccessTokenContainer()
            mockWebServer = MockWebServer()
            authenticator = DefaultAuthenticator("test",
                    AuthenticatorConfiguration(OkHttpClient(), mockWebServer.hostName, "id", "secret").apply {
                        customPort = mockWebServer.port
                    },
                    accessTokenContainer
            )
        }

        @After
        fun tearDown() {
            mockWebServer.shutdown()
        }

        @Test
        fun createAuthRequest() {
            given("Have not access token") {
                mockWebServer.enqueue(MockResponse().setResponseCode(200)
                        .setBody("{\"access_token\":\"response access token\",\"token_type\":\"bearer\",\"expires_in\":7200,\"scope\":\"public\"}"))

                val authRequest = authenticator.createAuthRequest(
                        {
                            // do wrap request
                        },
                        {
                            fail(it.exception.message)
                        },
                        {it}
                )
                on("Do auth request and succeed") {
                    authRequest.execute(
                            {
                                authRequest.invoker!!.callbackSuccess.invoke(it)
                            },
                            {
                                fail(it.exception.message)
                            }
                    )
                    it("should saved access token") {
                        assertThat(accessTokenContainer.accessToken)
                                .isEqualTo("response access token")
                    }
                    it("should send correction header") {
                        mockWebServer.takeRequest().let {
                            assertThat(it.getHeader("Authorization")).isEqualTo("Basic aWQ6c2VjcmV0")
                        }
                    }
                }
            }
        }
    }

    @RunWith(RobolectricTestRunner::class)
    @Config(manifest = Config.NONE)
    class RequestPreparing {
        lateinit var accessTokenContainer: OnMemoryAccessTokenContainer

        lateinit var authenticator: DefaultAuthenticator

        @Before
        fun setUp() {
            accessTokenContainer = OnMemoryAccessTokenContainer()
            authenticator = DefaultAuthenticator("test",
                    AuthenticatorConfiguration(OkHttpClient(), "auth.test.com", "id", "secret"),
                    accessTokenContainer
            )
        }

        @Test
        fun requestPreparingDoNothing() {
            given("Have not access token") {
                accessTokenContainer.accessToken = ""
                on("Prepare request") {
                    val builder = Request.Builder().url("http://test.com")
                    authenticator.requestPreparing(builder)
                    it("Do not added Authorization header") {
                        assertThat(builder.build().header("Authorization")).isNull()
                    }
                }
            }
        }

        @Test
        fun requestPreparing() {
            given("Have a access token") {
                accessTokenContainer.accessToken = "aaa"
                on("Prepare request") {
                    val builder = Request.Builder().url("http://test.com")
                    authenticator.requestPreparing(builder)
                    it("Add Authorization header") {
                        assertThat(builder.build().header("Authorization"))
                                .isEqualTo("Bearer aaa")
                    }
                }
            }
        }
    }

}
