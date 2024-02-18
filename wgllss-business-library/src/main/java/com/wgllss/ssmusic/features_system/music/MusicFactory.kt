package com.wgllss.ssmusic.features_system.music

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.MediaBrowserServiceCompat
import com.wgllss.core.units.WLog
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import com.wgllss.ssmusic.features_system.services.MusicService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 音乐播放工厂，处理多音乐功能，主要负责 播放列表取数据源 ，处理上下一曲数据源
 * appViewModel:主持提供各种数据
 */

class MusicFactory constructor(context: Context, private val appViewModel: AppViewModel) : MusicComponent(context) {

    override fun onCreate(musicService: MusicService) {
        super.onCreate(musicService)
        appViewModel?.run {
            queryPlayList()
            metadataPrepareCompletion.observe(this@MusicFactory) {
                val title = if (it.dataSourceType == 0) "${it.title}(高品质)" else it.title
                preparePlay(it.id.toString(), title, it.author, it.pic, it.url, whenReady)
            }
        }
    }

    private var mSendResultCalled = false

    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (MEDIA_ID_ROOT == parentId) {
            WLog.e(this, "onLoadChildren parentId 333: $parentId")
            appViewModel.run {
                isInitSuccess.observe(this@MusicFactory) {
                    it.takeIf {
                        it == true
                    }?.let {
                        liveData.observe(this@MusicFactory) { list ->
                            if (!whenReady)
                                getPlayUrlFromMediaID(MMKVHelp.getCurrentMediaId() ?: "")
                            serviceScope.launch {
                                if (!mSendResultCalled) {
                                    val child = withContext(IO) {
                                        list.map { m ->
                                            val title = if (m.dataSourceType == 0) "${m.title}(高品质)" else m.title
                                            MediaBrowserCompat.MediaItem(
                                                MediaDescriptionCompat.Builder()
                                                    .setMediaId(m.id.toString())
                                                    .setTitle(title)
                                                    .setIconUri(Uri.parse(m.pic))
                                                    .setMediaUri(Uri.parse(m.url))
                                                    .setSubtitle(m.author)
                                                    .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                                            )
                                        }?.toMutableList()
                                    }
                                    try {
                                        result.sendResult(child)
                                        mSendResultCalled = true
                                        WLog.e(this@MusicFactory, " result.sendResult(child) $parentId  ")
                                    } catch (e: Exception) {
                                        mSendResultCalled = false
                                        WLog.e(this@MusicFactory, "Exception e： ${e.message}")
                                    }
                                } else {
                                    mSendResultCalled = false
                                    musicService.notifyChildrenChanged(parentId)
                                    WLog.e(this@MusicFactory, "notifyChildrenChanged $parentId  ")
                                }
                            }
                        }
                    }
                }
            }
        } else {

        }
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        WLog.e(this, "onPrepareFromMediaId mediaId: $mediaId playWhenReady: $playWhenReady  extras:${Thread.currentThread().name}")
        appViewModel.getPlayUrlFromMediaID(mediaId)
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        super.onPrepareFromUri(uri, playWhenReady, extras)
        extras?.run {
            appViewModel.putToCache(getString(Constants.MEDIA_ID_KEY) ?: "", getString(Constants.MEDIA_URL_KEY) ?: "")
        }
        appViewModel.getCacheURL()
    }

    override fun playNext() {
        appViewModel.playNext()
    }

    override fun playPrevious() {
        appViewModel.playPrevious()
    }

}