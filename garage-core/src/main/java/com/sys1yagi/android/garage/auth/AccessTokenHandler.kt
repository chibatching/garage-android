package com.sys1yagi.android.garage.auth

interface AccessTokenHandler {

    fun shouldAuthentication(accessTokenHolder: AccessTokenHolder): Boolean
}
