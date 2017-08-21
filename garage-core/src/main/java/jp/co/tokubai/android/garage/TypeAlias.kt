package jp.co.tokubai.android.garage

import okhttp3.Request

typealias RequestBefore = (Request.Builder) -> Request.Builder
