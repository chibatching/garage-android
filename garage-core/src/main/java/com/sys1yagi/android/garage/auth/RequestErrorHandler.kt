package com.sys1yagi.android.garage.auth

import okhttp3.Call
import okhttp3.Response

interface RequestErrorHandler {

    fun shouldAuthentication(call: Call, response: Response): Boolean
}
