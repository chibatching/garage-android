package com.sys1yagi.android.garage.core.auth

import com.sys1yagi.android.garage.core.request.GarageError
import com.sys1yagi.android.garage.core.request.GarageRequest
import com.sys1yagi.android.garage.core.request.GarageResponse
import com.sys1yagi.android.garage.core.request.GetRequest
import okhttp3.Request

interface Authenticator {
    fun shouldAuthentication(request: GetRequest): Boolean
    fun shouldAuthentication(response: GarageResponse): Boolean
    fun createAuthRequest(success: (GarageResponse) -> Unit, failed: (GarageError) -> Unit, requestPreparing: (Request.Builder) -> Request.Builder): GarageRequest
    fun requestPreparing(builder: Request.Builder): Request.Builder
}
