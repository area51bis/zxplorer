package com.ses.app.zxbrowser.zxcollection

open class IdText<T : Any> {
    lateinit var id: T
    lateinit var text: String

    override fun toString(): String = text
}