package com.wgllss.ssmusic.features_system.startup

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
//import com.wgllss.ssmusic.NavigationConfig
import com.wgllss.ssmusic.core.activity.ActivityManager
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_third.um.UMHelp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InitHomeInitialize : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            LogTimer.LogE(this@InitHomeInitialize, "create ${Thread.currentThread().name}")
            MMKV.initialize(context)
            UMHelp.umInit(context)
            LogTimer.LogE(this@InitHomeInitialize, "create 1 ${Thread.currentThread().name}")
        }
    }
}