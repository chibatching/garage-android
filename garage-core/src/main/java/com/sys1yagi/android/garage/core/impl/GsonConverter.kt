package com.sys1yagi.android.garage.core.impl

import com.google.gson.Gson
import com.sys1yagi.android.garage.core.json.Converter
import java.lang.reflect.Type

class GsonConverter(val gson: Gson) : Converter {
    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return gson.fromJson(json, clazz)
    }

    override fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    override fun <T> toJson(t: T): String {
        return gson.toJson(t)
    }
}
