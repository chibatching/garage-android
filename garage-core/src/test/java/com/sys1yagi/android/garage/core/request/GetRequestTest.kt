package com.sys1yagi.android.garage.core.request

import com.sys1yagi.android.garage.core.config.RequestConfiguration
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GetRequestTest {

    @Test
    fun buildHttpUrl() {
        val request = GetRequest(Path("v1", "users"), RequestConfiguration(OkHttpClient(), "test.com"))
        assertThat(request.url()).isEqualTo("http://test.com:80/v1/users")
    }

    @Test
    fun buildHttpsUrl() {
        val request = GetRequest(Path("v1", "users"), RequestConfiguration(OkHttpClient(), "test.com").apply {
            scheme = Scheme.HTTPS
        })
        assertThat(request.url()).isEqualTo("https://test.com:443/v1/users")
    }

    @Test
    fun buildHttpUrlWithParameter() {
        val request = GetRequest(Path("v1", "users"), RequestConfiguration(OkHttpClient(), "test.com")).apply {
            parameter = Parameter()
                    .append("version_code", 10)
                    .append("type", "android")
        }
        assertThat(request.url()).isEqualTo("http://test.com:80/v1/users?version_code=10&type=android")
    }

    @Test
    fun escapeParameters(){
        val request = GetRequest(Path("v1", "users"), RequestConfiguration(OkHttpClient(), "test.com")).apply {
            parameter = Parameter()
                    .append("version_code", 10)
                    .append("comment", "hello world&kotlin")
        }
        assertThat(request.url()).isEqualTo("http://test.com:80/v1/users?version_code=10&comment=hello+world%26kotlin")
    }
}
