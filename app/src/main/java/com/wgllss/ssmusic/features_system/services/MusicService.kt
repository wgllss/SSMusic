package com.wgllss.ssmusic.features_system.services

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.features_system.globle.Constants.APP_PACKAGE_NAME
import com.wgllss.ssmusic.features_system.globle.Constants.CALLER_OTHER
import com.wgllss.ssmusic.features_system.globle.Constants.CALLER_SELF
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.music.MusicFactory
import com.wgllss.ssmusic.features_system.music.impl.mediaplayer.MediaID
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
        return BrowserRoot(MediaID(MEDIA_ID_ROOT, null, if (clientPackageName == APP_PACKAGE_NAME) CALLER_SELF else CALLER_OTHER).asString(), null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        musicFactory?.get()?.onLoadChildren(parentId, result)
    }
}