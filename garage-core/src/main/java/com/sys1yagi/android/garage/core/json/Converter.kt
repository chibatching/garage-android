package com.sys1yagi.android.garage.core.json

import java.lang.reflect.Type

interface Converter {
    fun <T> fromJson(json: String, clazz: Class<T>): T
    fun <T> fromJson(json: String, type: Type): T
    fun <T> toJson(t: T): String
}
