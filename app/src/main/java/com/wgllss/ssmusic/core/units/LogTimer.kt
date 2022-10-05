package com.wgllss.ssmusic.core.units

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object LogTimer {
    var time: Long = System.currentTimeMillis()

    fun initTime(any: Any) {
//        map.put(any.javaClass.simpleName, System.currentTimeMillis())
        time = System.currentTimeMillis()
    }


//    fun LogE(any: Any, tagName: String) {
//        GlobalScope.launch {
//            val cur = System.currentTimeMillis()
//            if (map.containsKey(any.javaClass.simpleName)) {
//                val dis = cur - map[any.javaClass.simpleName]!!
//                Log.e("${any.javaClass.simpleName}", " ${tagName} 耗时:${dis} ms")
//            }
//        }
//    }

    fun LogE(any: Any, tagName: String) {
        GlobalScope.launch {
            val cur = System.currentTimeMillis()
            val dis = cur - time
//            time = cur
//            if (dis > 50)
            Log.e("${any.javaClass.simpleName}", " ${tagName} 耗时:${dis} ms")
        }
    }
}