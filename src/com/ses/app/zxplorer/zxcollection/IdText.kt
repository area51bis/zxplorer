package com.ses.app.zxplorer.zxcollection

open class IdText<T : Any> {
    lateinit var id: T
    lateinit var text: String

    override fun toString(): String = text
}