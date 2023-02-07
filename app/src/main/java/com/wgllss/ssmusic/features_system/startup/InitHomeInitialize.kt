package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_third.um.UMHelp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InitHomeInitialize : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            MMKV.initialize(context)
            UMHelp.umInit(context)
        }
    }
}