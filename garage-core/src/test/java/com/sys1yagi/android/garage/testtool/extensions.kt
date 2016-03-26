package com.sys1yagi.android.garage.testtool

import okhttp3.mockwebserver.MockWebServer
import java.util.concurrent.TimeUnit


data class TimeUnitValue(val value: Long, val unit: TimeUnit)

val Int.milliseconds: TimeUnitValue
    get() = TimeUnitValue(this.toLong(), TimeUnit.MILLISECONDS)

fun MockWebServer.takeRequest(timeUnitValue: TimeUnitValue) =
        takeRequest(timeUnitValue.value, timeUnitValue.unit)

