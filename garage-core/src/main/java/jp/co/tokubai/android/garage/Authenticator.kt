package jp.co.tokubai.android.garage

import okhttp3.Request

interface Authenticator {
    fun authenticationIfNeeded()
    fun requestPreparing(builder: Request.Builder): Request.Builder
}