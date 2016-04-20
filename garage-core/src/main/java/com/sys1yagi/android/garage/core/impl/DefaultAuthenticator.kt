package com.sys1yagi.android.garage.core.impl

import android.text.TextUtils
import android.util.Base64
import com.google.gson.Gson
import com.sys1yagi.android.garage.auth.AuthResponseBody
import com.sys1yagi.android.garage.core.auth.AccessTokenContainer
import com.sys1yagi.android.garage.core.auth.Authenticator
import com.sys1yagi.android.garage.core.config.AuthenticatorConfiguration
import com.sys1yagi.android.garage.core.request.*
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.net.HttpURLConnection

open class DefaultAuthenticator(var userName: String, private val config: AuthenticatorConfiguration, private val container: AccessTokenContainer) : Authenticator {

    companion object {
        val MEDIA_TYPE_FORM_URLENCODED: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    }

    override fun shouldAuthentication(request: GarageRequest): Boolean {
        return isEmpty(container) or isExpired(request.requestTime(), container)
    }

    internal fun isEmpty(accessTokenHolder: AccessTokenContainer): Boolean {
        return TextUtils.isEmpty(accessTokenHolder.accessToken)
    }

    internal fun isExpired(now: Long, accessTokenHolder: AccessTokenContainer): Boolean =
            now >= accessTokenHolder.savedAt + accessTokenHolder.expired * 1000

    override fun shouldAuthentication(response: GarageResponse): Boolean {
        return response.response.code() == HttpURLConnection.HTTP_UNAUTHORIZED
    }

    override fun createAuthRequest(success: (GarageResponse) -> Unit, failed: (GarageError) -> Unit, requestPreparing: (Request.Builder) -> Request.Builder): PostRequest {
        return PostRequest(Path("", "oauth/token"),
                RequestBody.create(MEDIA_TYPE_FORM_URLENCODED,
                        Parameter()
                                .append("grant_type", "password")
                                .append("username", userName)
                                .build()),
                config,
                { builder ->
                    requestPreparing.invoke(builder)
                    builder.addHeader("Authorization", "Basic " + String(Base64.encode("${config.applicationId}:${config.applicationSecret}".toByteArray(), Base64.NO_WRAP)))
                }
        ).apply {
            invoker = GarageRequest.Invoker(
                    { garageResponse ->
                        if (garageResponse.response.isSuccessful) {
                            println("succeess")
                            val body = Gson().fromJson(garageResponse.response.body().string(), AuthResponseBody::class.java)
                            container.accessToken = body.accessToken
                            container.savedAt = System.currentTimeMillis()
                            container.expired = body.expiresIn.toLong()
                            success.invoke(garageResponse)
                        } else {
                            failed.invoke(GarageError().apply {
                                this.call = garageResponse.call
                                this.response = garageResponse.response
                            })
                        }
                    },
                    { error ->
                        failed.invoke(error)
                    }
            )
        }
    }

    override fun requestPreparing(builder: Request.Builder): Request.Builder {
        if (TextUtils.isEmpty(container.accessToken)) {
            return builder
        } else {
            return builder.header("Authorization", "Bearer ${container.accessToken}")
        }
    }
}
