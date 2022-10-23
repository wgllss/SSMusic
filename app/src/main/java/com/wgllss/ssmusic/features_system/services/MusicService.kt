package com.wgllss.ssmusic.features_system.services

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.wgllss.ssmusic.features_system.music.MusicFactory
import com.wgllss.ssmusic.features_system.music.impl.mediaplayer.MediaID
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {
    companion object {
        const val CALLER_SELF = "self"
        const val CALLER_OTHER = "other"
        const val APP_PACKAGE_NAME = "com.wgllss.ssmusic"
        const val MEDIA_ID_ROOT = -1
    }

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

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        val caller = if (clientPackageName == APP_PACKAGE_NAME) {
            CALLER_SELF
        } else {
            CALLER_OTHER
        }
        return MediaBrowserServiceCompat.BrowserRoot(MediaID(MEDIA_ID_ROOT.toString(), null, caller).asString(), null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
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