package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.ssmusic.NavigationConfig
import com.wgllss.ssmusic.core.units.AppConfig
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.dl.InitializerEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class InitHomeInitialize : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            val appViewModelL = async(Dispatchers.IO) {
                InitializerEntryPoint.resolve(context).injectAppViewModel().get()
            }
            val time = measureTimeMillis {
                NavigationConfig.getDestConfig()
//                AppConfig.getDestConfig(context)
            }
//            appViewModelL.await().installHomeJson.postValue(true)
            WLog.e(this@InitHomeInitialize, "time ${time} ms")
            LogTimer.LogE(this@InitHomeInitialize, "getDestConfig")
        }
    }
}