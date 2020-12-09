package com.ses.app.zxplorer

import javafx.application.Platform
import javafx.scene.control.ProgressBar

typealias ProgressHandler = (progress: Double, status: Any?, message: String?) -> Unit

class ProgressManager private constructor(
        private val parent: ProgressManager? = null,
        private val total: Float = 1.0f,
        private val progressHandler: ProgressHandler? = null) {

    companion object {
        val INDETERMINATE_PROGRESS = ProgressBar.INDETERMINATE_PROGRESS

        var current: ProgressManager? = null

        fun new(progressHandler: ProgressHandler?): ProgressManager {
            return ProgressManager(progressHandler = progressHandler).also {
                current = it
            }
        }
    }

    fun notify(progress: Double, status: Any? = null, message: String? = null) {
        Platform.runLater { progressHandler?.invoke(progress, status, message) }
    }

    fun end() {
        if (this == current) current = null
    }
}
