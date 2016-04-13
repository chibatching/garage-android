package com.sys1yagi.android.garage.core.request

import android.text.TextUtils

open class Path(private val versionName: String, private val path: String) {

    fun to(): String {
        if (TextUtils.isEmpty(versionName)) {
            return path
        } else {
            return "$versionName/$path"
        }
    }
}
