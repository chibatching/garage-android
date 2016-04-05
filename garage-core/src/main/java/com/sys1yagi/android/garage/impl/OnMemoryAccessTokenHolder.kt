package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.auth.AccessTokenHolder

class OnMemoryAccessTokenHolder : AccessTokenHolder {
    override var accessToken: String? = null
        get() = field
        set(value) {
            field = value
        }
    override var expried: Long? = 0L
        get() = field
        set(value) {
            field = value
        }
    override var savedAt: Long? = 0L
        get() = field
        set(value) {
            field = value
        }
}
