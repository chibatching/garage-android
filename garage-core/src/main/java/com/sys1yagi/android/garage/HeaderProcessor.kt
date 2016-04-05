package com.sys1yagi.android.garage

import okhttp3.Request

interface HeaderProcessor {
    fun invoke(builder: Request.Builder): Unit {

    }
}
