package com.ses.util

object SysUtil {
    val osName by lazy { System.getProperty("os.name") }
    val isWindows: Boolean by lazy { osName.toLowerCase().contains("win") }
    val isLinux: Boolean by lazy { osName.toLowerCase().contains("linux") }
    val isMac: Boolean by lazy { osName.toLowerCase().contains("mac") }
}
