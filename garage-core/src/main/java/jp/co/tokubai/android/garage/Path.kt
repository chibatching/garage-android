package jp.co.tokubai.android.garage

open class Path(private val versionName: String, private val path: String) {

    open fun to(): String {
        return if (versionName.isEmpty()) {
            path
        } else {
            "$versionName/$path"
        }
    }
}
