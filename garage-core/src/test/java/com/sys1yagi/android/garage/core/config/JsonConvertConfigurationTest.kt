package com.sys1yagi.android.garage.core.config

import com.google.gson.Gson
import com.sys1yagi.android.garage.core.impl.GsonConverter
import org.junit.Test


class JsonConvertConfigurationTest {
    @Test
    fun valid() {
        JsonConvertConfiguration(GsonConverter(Gson()))
    }
}
