package com.ses.app.zxplorer.filters

abstract class Filter<T> {
    abstract fun check(o: T): Boolean
}