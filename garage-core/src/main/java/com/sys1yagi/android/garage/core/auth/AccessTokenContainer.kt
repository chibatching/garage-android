package com.sys1yagi.android.garage.core.auth

interface AccessTokenContainer {
    var accessToken: String
    var expired: Long
    var savedAt: Long
}
