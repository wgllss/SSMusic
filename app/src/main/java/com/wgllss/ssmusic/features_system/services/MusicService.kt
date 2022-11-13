package com.wgllss.ssmusic.features_system.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.features_system.music.MusicFactory
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var musicFactory: Lazy<MusicFactory>

    inner class MusicBinder : Binder() {
        val musicService: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder? {
        musicFactory?.get()?.onResume()
        return MusicBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        musicFactory.get().onStop()
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        musicFactory.get().onCreate(this)
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
        intent?.let { musicFactory.get().handleCommandIntent(intent) }
        logE("onStartCommand intent ")
        return START_NOT_STICKY //no sense to use START_STICKY with using startForeground
//        return super.onStartCommand(intent, flags, startId)
    }
}