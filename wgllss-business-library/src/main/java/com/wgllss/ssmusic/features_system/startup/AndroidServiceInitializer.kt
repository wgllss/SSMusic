package com.wgllss.ssmusic.features_system.startup
//
//import android.content.Context
//import androidx.startup.Initializer
//
//class AndroidServiceInitializer : Initializer<Any> {
//
//    override fun create(context: Context) {
////        GlobalScope.launch {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                ServiceUtil.startForegroundService(context, MusicService::class.java)
////            } else {
////            ServiceUtil.startService(context, MusicService::class.java)
////            }
////        }
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return emptyList()
//    }
//
//}