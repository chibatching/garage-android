package com.sys1yagi.android.garage.auth

interface AccessTokenHolder {
    var accessToken: String
    var expried: Long
    var savedAt: Long
}
