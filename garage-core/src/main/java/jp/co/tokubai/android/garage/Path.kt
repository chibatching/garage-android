package jp.co.tokubai.android.garage

import android.text.TextUtils

open class Path(private val versionName: String, private val path: String) {

    open fun to(): String {
        return if (TextUtils.isEmpty(versionName)) {
            path
        } else {
            "$versionName/$path"
        }
    }
}
