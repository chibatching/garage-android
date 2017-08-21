package jp.co.tokubai.android.garage

import android.text.TextUtils

open class Path(private val versionName: String, private val path: String) {

    open fun to(): String {
        if (TextUtils.isEmpty(versionName)) {
            return path
        } else {
            return "$versionName/$path"
        }
    }

    // TODO
    // for test
    constructor() : this("", "")
}
