package com.sys1yagi.android.garage.core.config

import com.google.gson.Gson
import com.sys1yagi.android.garage.core.executor.Executor
import com.sys1yagi.android.garage.core.impl.GsonConverter
import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GarageConfigurationTest {

    @Test
    fun configuration() {
        val client = OkHttpClient()
        GarageConfiguration(
                RequestConfiguration(
                        client, "test.com"
                ),
                ExecutorConfiguration(Executor()),
                JsonConvertConfiguration(GsonConverter(Gson()))
        )
    }
}
