package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.core.units.LogTimer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadPluginInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            emptyInit(context)
        }
    }

    private fun emptyInit(context: Context) {
        try {
            LogTimer.LogE(this@LoadPluginInitializer, "create ${Thread.currentThread().name}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}