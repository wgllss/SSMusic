package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.startup.Initializer
import com.wgllss.ssmusic.core.units.ServiceUtil
import com.wgllss.ssmusic.features_system.services.MusicService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AndroidServiceInitializer : Initializer<Any> {

    override fun create(context: Context) {
        GlobalScope.launch {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ServiceUtil.startForegroundService(context, MusicService::class.java)
//            } else {
//            ServiceUtil.startService(context, MusicService::class.java)
//            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}