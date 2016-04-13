package com.sys1yagi.android.garage.core.request

import okhttp3.Call

open class GarageError {
    lateinit var call: Call
    lateinit var exception: Exception
}
