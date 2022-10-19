package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.Intent
import androidx.startup.Initializer
import com.wgllss.ssmusic.core.units.ServiceUtil
import com.wgllss.ssmusic.features_system.services.MusicService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AndroidServiceInitializer : Initializer<Any> {
    override fun create(context: Context) {
        GlobalScope.launch {
            ServiceUtil.startService(context,MusicService::class.java)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}