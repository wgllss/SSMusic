package com.wgllss.ssmusic.core.units

object WLog {

    fun e(any: Any, message: String) {
        android.util.Log.e(any.javaClass.simpleName, message)
    }
}