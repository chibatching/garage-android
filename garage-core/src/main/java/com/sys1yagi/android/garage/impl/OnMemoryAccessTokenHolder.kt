package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.auth.AccessTokenHolder

class OnMemoryAccessTokenHolder : AccessTokenHolder {
    override var accessToken: String = ""
    override var expried: Long = 0L
    override var savedAt: Long = 0L
}
