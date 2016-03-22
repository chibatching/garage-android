package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.GarageClient
import com.sys1yagi.android.garage.Path
import com.sys1yagi.android.garage.auth.Authenticator
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class DefaultAuthenticator : Authenticator {
    override fun authenticate(garageClient: GarageClient, success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit) {
        garageClient.get(Path("v1", "oauth/token"))
    }
}
