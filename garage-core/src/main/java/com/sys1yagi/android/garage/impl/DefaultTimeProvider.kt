package com.sys1yagi.android.garage.impl

import com.sys1yagi.android.garage.util.TimeProvider

class DefaultTimeProvider : TimeProvider {
    override fun now(): Long {
        return System.currentTimeMillis()
    }
}
