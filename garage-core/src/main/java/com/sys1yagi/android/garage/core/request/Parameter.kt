package com.sys1yagi.android.garage.core.request

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
                        "${it.first}=${it.second}"
                    }
                    .reduce {
                        a, b ->
                        "$a&$b"
                    }

}

