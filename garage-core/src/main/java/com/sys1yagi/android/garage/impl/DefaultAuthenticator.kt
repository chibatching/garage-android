package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.GarageClient
import com.sys1yagi.android.garage.Path
import com.sys1yagi.android.garage.auth.Authenticator
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class DefaultAuthenticator : Authenticator {

    companion object {
        val MEDIA_TYPE_FORM_URLENCODED: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    }

    override fun authenticate(garageClient: GarageClient, success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit) {
        garageClient.post(Path("", "oauth/token"),
                RequestBody.create(MEDIA_TYPE_FORM_URLENCODED, ""))
                .enqueue(success, failed)
    }

    fun header() {

    }

}
