package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
import com.wgllss.core.units.LogTimer
import com.wgllss.plugin.library.HomePluginHelp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitHomeFirstInitialize : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                HomePluginHelp(SampleHomeDownLoadFace()).initDynamicPlugin(activity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}