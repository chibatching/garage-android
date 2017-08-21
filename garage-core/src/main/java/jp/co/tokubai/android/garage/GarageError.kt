package jp.co.tokubai.android.garage

import okhttp3.Call
import okhttp3.Response

open class GarageError(e: Throwable?) : Exception(e) {
    var call: Call? = null
    var response: Response? = null
}
