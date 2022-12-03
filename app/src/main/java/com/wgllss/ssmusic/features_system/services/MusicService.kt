package com.wgllss.ssmusic.features_system.services

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.music.MusicFactory
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var musicFactory: Lazy<MusicFactory>

    override fun onCreate() {
        super.onCreate()
        musicFactory.get().onCreate(this)
        sessionToken = musicFactory.get().mediaSession.sessionToken

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

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        logE("onGetRoot clientPackageName: $clientPackageName")
        musicFactory?.get()?.onGetRoot()
        return BrowserRoot(MEDIA_ID_ROOT, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        musicFactory?.get()?.onLoadChildren(parentId, result)
    }
}