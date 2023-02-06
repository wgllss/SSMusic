package com.wgllss.ssmusic.features_system.startup
//
//import android.content.Context
//import androidx.startup.Initializer
//import com.tencent.mmkv.MMKV
//import com.wgllss.ssmusic.core.ex.logE
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//
//
//class MMKVInitializer : Initializer<Unit> {
//
//    override fun create(context: Context) {
//        logE("MMKVInitializer .create()")
//        GlobalScope.launch {
//            val rootDir: String = MMKV.initialize(context)
////            logE("mmkv root: $rootDir")
//        }
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return emptyList()
//    }
//}