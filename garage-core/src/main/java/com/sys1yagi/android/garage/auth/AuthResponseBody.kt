package com.sys1yagi.android.garage.auth

import com.google.gson.annotations.SerializedName

//{"access_token":"TOKEN","token_type":"bearer","expires_in":7200,"scope":"public"}
class AuthResponseBody {

    @SerializedName("access_token")
    lateinit var accessToken: String

    @SerializedName("token_type")
    lateinit var tokenType: String

    @SerializedName("expires_in")
    var expiresIn: Int = 0

    @SerializedName("scope")
    lateinit var scope: String

}
