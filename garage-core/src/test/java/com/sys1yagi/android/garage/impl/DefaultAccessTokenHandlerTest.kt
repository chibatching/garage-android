package com.sys1yagi.android.garage.impl


import com.sys1yagi.android.garage.util.TimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultAccessTokenHandlerTest {

    class TestTimeProvider(var now: Long) : TimeProvider {
        override fun now(): Long = now
    }

    val testTimeProvider = TestTimeProvider(0)

    val handler = DefaultAccessTokenHandler(testTimeProvider)

    @Test
    fun isEmptyFalse() {
        assertThat(
                handler.isEmpty(OnMemoryAccessTokenHolder().apply {
                    accessToken = "test"
                })
        ).isFalse()
    }

    @Test
    fun isEmptyTrue() {
        assertThat(handler.isEmpty(OnMemoryAccessTokenHolder())).isTrue()
    }

    @Test
    fun isExpiredFalse() {
        assertThat(
                handler.isExpired(20, OnMemoryAccessTokenHolder().apply {
                    savedAt = 10
                    expried = 10
                })
        ).isTrue()
    }

    @Test
    fun isExpiredTrue() {
        assertThat(
                handler.isExpired(20, OnMemoryAccessTokenHolder().apply {
                    savedAt = 10
                    expried = 11
                })
        ).isFalse()
    }

    @Test
    fun shouldAuthenticationTrue() {
        assertThat(
                handler.shouldAuthentication(OnMemoryAccessTokenHolder().apply {
                })
        ).isTrue()

        testTimeProvider.now = 22
        assertThat(
                handler.shouldAuthentication(OnMemoryAccessTokenHolder().apply {
                    accessToken = "test"
                    savedAt = 10
                    expried = 11
                })
        ).isTrue()
    }

    @Test
    fun shouldAuthenticationFalse() {
        testTimeProvider.now = 22
        assertThat(
                handler.shouldAuthentication(OnMemoryAccessTokenHolder().apply {
                    accessToken = "test"
                    savedAt = 10
                    expried = 11
                })
        ).isTrue()
    }
}
