package com.sys1yagi.android.garage

enum class Scheme {
    HTTP("http", 80),
    HTTPS("https", 443);

    lateinit var string: String
    var defaultPort: Int = 0

    constructor(string: String, defaultPort: Int) {
        this.string = string
        this.defaultPort = defaultPort;
    }
}
