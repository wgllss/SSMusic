package com.wgllss.ssmusic.features_system.services

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.music.MusicFactory

class MusicService : MediaBrowserServiceCompat() {

    private val musicFactory by lazy { MusicFactory(this, AppViewModel.getInstance(this.application)) }

    override fun onCreate() {
        super.onCreate()
        musicFactory.onCreate(this)
        sessionToken = musicFactory.mediaSession.sessionToken

    }

    override fun onStart(intent: Intent?, startId: Int) {
        musicFactory.onStart()
        super.onStart(intent, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        musicFactory?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicFactory?.onDestory()
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        musicFactory?.onGetRoot()
        return BrowserRoot(MEDIA_ID_ROOT, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        musicFactory?.onLoadChildren(parentId, result)
    }
}