package com.sys1yagi.android.garage

import okhttp3.OkHttpClient
import org.junit.Test

class GarageConfigurationTest {

    @Test
    fun requiredConfiguration() {
        val config = GarageConfiguration.make {
            this.applicationId = "a"
            this.applicationSecret = "b"
            this.endpoint = "c"
            this.client = OkHttpClient()
        }
    }
}
