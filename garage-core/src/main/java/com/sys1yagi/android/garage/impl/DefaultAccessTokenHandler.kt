package com.sys1yagi.android.garage.impl

import android.text.TextUtils
import com.sys1yagi.android.garage.auth.AccessTokenHandler
import com.sys1yagi.android.garage.auth.AccessTokenHolder
import com.sys1yagi.android.garage.util.TimeProvider

class DefaultAccessTokenHandler(val timeProvider: TimeProvider) : AccessTokenHandler {
    override fun shouldAuthentication(accessTokenHolder: AccessTokenHolder): Boolean {
        return isEmpty(accessTokenHolder) or isExpired(timeProvider.now(), accessTokenHolder)
    }

    fun isEmpty(accessTokenHolder: AccessTokenHolder): Boolean =
            TextUtils.isEmpty(accessTokenHolder.accessToken)

    fun isExpired(now: Long, accessTokenHolder: AccessTokenHolder): Boolean =
            now >= accessTokenHolder.savedAt + accessTokenHolder.expried
}
