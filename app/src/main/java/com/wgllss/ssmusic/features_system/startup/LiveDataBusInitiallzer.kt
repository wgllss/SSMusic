package com.wgllss.ssmusic.features_system.startup
//
//import android.content.Context
//import androidx.startup.Initializer
//import com.jeremyliao.liveeventbus.LiveEventBus
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//
//class LiveDataBusInitiallzer : Initializer<Unit> {
//
//    override fun create(context: Context) {
//        GlobalScope.launch {
//            LiveEventBus.config().autoClear(true)
//                .lifecycleObserverAlwaysActive(false)
//        }
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return emptyList()
//    }
//}