package com.wgllss.ssmusic.features_system.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wgllss.ssmusic.features_system.music.MusicFactory
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var musicFactory: Lazy<MusicFactory>

    override fun onBind(intent: Intent): IBinder? {
        musicFactory?.get()?.onResume()
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        musicFactory.get().onStop()
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        musicFactory.get().onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        musicFactory.get().onStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicFactory?.get()?.onDestory()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { musicFactory.get().handlerIntent(intent) }
        return super.onStartCommand(intent, flags, startId)
//        return START_NOT_STICKY //no sense to use START_STICKY with using startForeground
    }
}