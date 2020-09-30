package com.ses.app.zxlauncher.filters

abstract class Filter<T> {
    abstract fun check(o: T): Boolean
}