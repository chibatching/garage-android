package com.sys1yagi.android.garage.core.request

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PathTest {

    @Test
    fun buildPath() {
        assertThat(Path("v1", "users").to()).isEqualTo("v1/users")
        assertThat(Path("", "users").to()).isEqualTo("users")
    }
}
