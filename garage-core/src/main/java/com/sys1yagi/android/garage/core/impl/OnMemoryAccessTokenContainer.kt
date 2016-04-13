package com.sys1yagi.android.garage.core.impl

import com.sys1yagi.android.garage.core.auth.AccessTokenContainer

class OnMemoryAccessTokenContainer : AccessTokenContainer {
    override var accessToken: String = ""
    override var expired: Long = 0
    override var savedAt: Long = 0
}
