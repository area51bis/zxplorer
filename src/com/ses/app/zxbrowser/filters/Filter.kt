package com.ses.app.zxbrowser.filters

abstract class Filter<T> {
    abstract fun check(o: T): Boolean
}