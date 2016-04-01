package com.sys1yagi.android.garage.impl

import android.util.Base64
import com.sys1yagi.android.garage.GarageClient
import com.sys1yagi.android.garage.GarageConfiguration
import com.sys1yagi.android.garage.Parameter
import com.sys1yagi.android.garage.Path
import com.sys1yagi.android.garage.auth.AuthResponseBody
import com.sys1yagi.android.garage.auth.Authenticator
import okhttp3.*
import java.io.IOException

class DefaultAuthenticator(val userName: String) : Authenticator {

    companion object {
        val MEDIA_TYPE_FORM_URLENCODED: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    }

    override fun authenticate(garageClient: GarageClient, success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit) {
        garageClient.post(Path("", "oauth/token"),
                RequestBody.create(MEDIA_TYPE_FORM_URLENCODED,
                        Parameter()
                                .append("grant_type", "password")
                                .append("username", userName)
                                .build()),
                header(garageClient.configuration)
        )
                .setMaxRetryCount(0)
                .enqueue(
                        { c, r ->
                            parseResponse(garageClient, r)
                            success(c, r)
                        }, failed)
    }

    internal fun parseResponse(garageClient: GarageClient, response: Response) {
        if (response.isSuccessful) {
            val body = garageClient.configuration.gson.fromJson(response.body().string(), AuthResponseBody::class.java)
            garageClient.configuration.accessTokenHolder.accessToken = body?.accessToken
        }
    }

    fun header(configuration: GarageConfiguration): (Request.Builder) -> Request.Builder {
        return {
            it.addHeader("Authorization", "Basic "
                    + String(Base64.encode("${configuration.applicationId}:${configuration.applicationSecret}".toByteArray(), Base64.NO_WRAP)))
        }
    }

}
