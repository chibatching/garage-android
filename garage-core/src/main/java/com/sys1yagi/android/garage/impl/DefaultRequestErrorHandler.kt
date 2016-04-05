package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.auth.RequestErrorHandler
import okhttp3.Call
import okhttp3.Response
import java.net.HttpURLConnection

class DefaultRequestErrorHandler : RequestErrorHandler {

    override fun shouldAuthentication(call: Call, response: Response)
            = response.code() == HttpURLConnection.HTTP_UNAUTHORIZED

}
