package jp.co.tokubai.android.garage

enum class Scheme(val value: String, val port: Int) {
    HTTP("http", 80),
    HTTPS("https", 443);
}
