package com.sys1yagi.android.garage.testtool

import com.sys1yagi.android.garage.auth.AccessTokenHolder

class TestAccessTokenHolder(val _accessToken: String) : AccessTokenHolder {

    override var accessToken: String? = _accessToken
}
