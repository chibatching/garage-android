package com.sys1yagi.android.garage.core.testtool

internal fun given(description: String, given: Given.() -> Unit) {
    given.invoke(Given())
}

internal class Given {
    fun on(description: String, on: On.() -> Unit) {
        on.invoke(On())
    }
}

internal class On {
    fun it(description: String, it: It.() -> Unit) {
        it.invoke(It())
    }
}

internal class It {

}
