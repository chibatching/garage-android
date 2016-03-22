package com.sys1yagi.android.garage.auth

import com.sys1yagi.android.garage.GarageClient
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

interface Authenticator {

    fun authenticate(garageClient: GarageClient, success: (Call, Response) -> Unit, failed: (Call, IOException) -> Unit)
}
