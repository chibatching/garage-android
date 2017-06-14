package jp.co.tokubai.android.garage

import okhttp3.Request

interface Authenticator {
    fun authenticationIfNeeded(request: GetRequest2)
    fun requestPreparing(builder: Request.Builder): Request.Builder
}