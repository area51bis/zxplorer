package com.ses.app.zxbrowser

typealias ProgressHandler = (progress: Float, status: Any?, message: String?) -> Unit

class Progress private constructor(
        private val parent: Progress? = null,
        private val total: Float = 1.0f,
        private val progressHandler: ProgressHandler? = null) {

    companion object {
        var current: Progress? = null

        fun new(progressHandler: ProgressHandler?): Progress {
            return Progress(progressHandler = progressHandler).also {
                current = it
            }
        }
    }

    fun notify(progress: Float, status: Any? = null, message: String? = null) {
        progressHandler?.invoke(progress, status, message)
    }

    fun end() {
        if (this == current) current = null
    }
}
