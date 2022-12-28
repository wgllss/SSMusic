package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.ssmusic.NavigationConfig
import com.wgllss.ssmusic.core.units.LogTimer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InitHomeInitialize : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            NavigationConfig.getDestConfig()
        }
    }
}