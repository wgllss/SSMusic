package com.wgllss.ssmusic.features_system.music

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.MediaBrowserServiceCompat
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.services.MusicService
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 音乐播放工厂，处理多音乐功能，主要负责 播放列表取数据源 ，处理上下一曲数据源
 * appViewModel:主持提供各种数据
 */

class MusicFactory @Inject constructor(@ApplicationContext context: Context, private val appViewModel: Lazy<AppViewModel>) : MusicComponent(context) {

    override fun onCreate(musicService: MusicService) {
        super.onCreate(musicService)
        appViewModel.get().queryPlayList()
        appViewModel.get().metadataPrepareCompletion.observe(this) {
            preparePlay(it.id.toString(), it.title, it.author, it.pic, it.url)
        }
    }

    private var mSendResultCalled = false

    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (MEDIA_ID_ROOT == parentId) {
            logE("onLoadChildren parentId 333: $parentId")
            appViewModel.get().isInitSuccess.observe(this) {
                it.takeIf {
                    it == true
                }?.let {
                    appViewModel.get().liveData.observe(this@MusicFactory) { list ->
                        serviceScope.launch {
                            if (!mSendResultCalled) {
                                val child = withContext(IO) {
                                    list.map { musicTableBean ->
                                        MediaBrowserCompat.MediaItem(
                                            MediaDescriptionCompat.Builder()
                                                .setMediaId(musicTableBean.id.toString())
                                                .setTitle(musicTableBean.title)
                                                .setIconUri(Uri.parse(musicTableBean.pic))
                                                .setMediaUri(Uri.parse(musicTableBean.url))
                                                .setSubtitle(musicTableBean.author)
                                                .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                                        )
                                    }?.toMutableList()
                                }
                                try {
                                    result.sendResult(child)
                                    mSendResultCalled = true
                                    logE(" result.sendResult(child) $parentId  ")
                                } catch (e: Exception) {
                                    mSendResultCalled = false
                                    logE("Exception e： ${e.message}")
                                }
                            } else {
                                mSendResultCalled = false
                                musicService.notifyChildrenChanged(parentId)
                                logE("notifyChildrenChanged $parentId  ")
                            }
                        }
                    }
                }
            }
        } else {

        }
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        logE("onPrepareFromMediaId mediaId: $mediaId playWhenReady: $playWhenReady  extras:${Thread.currentThread().name}")
        appViewModel.get().getPlayUrlFromMediaID(mediaId)
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        super.onPrepareFromUri(uri, playWhenReady, extras)
        appViewModel.get().getCacheURL()
    }

    override fun playNext() {
        appViewModel.get().playNext()
    }

    override fun playPrevious() {
        appViewModel.get().playPrevious()
    }

}