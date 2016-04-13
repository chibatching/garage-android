package com.sys1yagi.android.garage.core.request

enum class Scheme(val value: String, val port: Int) {
    HTTP("http", 80),
    HTTPS("https", 443);
}
