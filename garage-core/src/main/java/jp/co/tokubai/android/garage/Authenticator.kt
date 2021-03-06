package jp.co.tokubai.android.garage

import okhttp3.Request
import okhttp3.Response

interface Authenticator {
    suspend fun authenticationIfNeeded(request: GarageRequest, before: RequestBefore)
    suspend fun authenticationIfNeeded(request: GarageRequest, response: Response): Boolean
    fun requestPreparing(builder: Request.Builder): Request.Builder
}
