package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.auth.AccessTokenHolder

class FileAccessTokenHolder : AccessTokenHolder {
    override var accessToken: String?
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var savedAt: Long?
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var expried: Long?
        get() = throw UnsupportedOperationException()
        set(value) {
        }
}
