package com.sys1yagi.android.garage

import okhttp3.OkHttpClient
import org.junit.Test

class GarageConfigurationTest {

    @Test
    fun requiredConfiguration() {
        val config = GarageConfiguration.make("a", "b", "c", OkHttpClient()) {
        }
    }
}
