package jp.co.tokubai.android.garage

import java.net.URLEncoder
import java.util.*

class Parameter {

    private val parameters = ArrayList<Pair<String, String>>()

    fun append(key: String, value: String): Parameter {
        parameters.add(Pair(key, value))
        return this
    }

    fun append(key: String, value: Int): Parameter = append(key, value.toString())

    fun append(key: String, value: Long): Parameter = append(key, value.toString())

    fun build(): String =
            parameters
                    .map {
                        "${it.first}=${URLEncoder.encode(it.second, "utf-8")}"
                    }
                    .reduce {
                        a, b ->
                        "$a&$b"
                    }

}

