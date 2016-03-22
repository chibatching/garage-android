package com.sys1yagi.android.garage.testtool

import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

class AssetsUtil {
    companion object {
        fun readString(path: String, root: String = "src/test/assets", charset: String = "utf-8"): String {
            return FileInputStream(File(root, path))
                    .reader(Charset.forName(charset))
                    .readText()
        }
    }
}
