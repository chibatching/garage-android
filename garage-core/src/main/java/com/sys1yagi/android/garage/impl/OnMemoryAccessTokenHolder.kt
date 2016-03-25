package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.auth.AccessTokenHolder

class OnMemoryAccessTokenHolder : AccessTokenHolder {
    override var accessToken: String? = null
        get() = field
        set(value) {
            field = value
        }
}
