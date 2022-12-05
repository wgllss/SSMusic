package com.wgllss.ssmusic.features_system.music

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.MimeTypes
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.dl.annotations.BindExoPlayer
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ARTNETWORK_URL_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_AUTHOR_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_TITLE_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_URL_KEY
import com.wgllss.ssmusic.features_system.music.extensions.*
import com.wgllss.ssmusic.features_system.services.MusicService
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject

/**
 * 音乐播放工厂，处理多音乐功能，
 * musicPlay:主持音乐各种操作
 * appViewModel:主持提供各种数据
 */

class MusicFactory @Inject constructor(@ApplicationContext context: Context, @BindExoPlayer private val musicPlay: Lazy<IMusicPlay>, private val appViewModel: Lazy<AppViewModel>) : MusicComponent(context) {

    private var jobc: Job? = null
    private var jobPlay: Job? = null

    private var isSendChild = false

    private val serviceJob by lazy { SupervisorJob() }
    private val serviceScope by lazy { CoroutineScope(Dispatchers.Main + serviceJob) }

    override fun onCreate(musicService: MusicService) {
        super.onCreate(musicService)
        appViewModel.get().queryPlayList()
        appViewModel.get().metadataList.observe(this) {
            preparePlaylist(it.id.toString(), it.title, it.author, it.pic, it.url)
        }
        appViewModel.get().currentPosition.observeForever {
            appViewModel.get().getDetail(it)
        }
    }

    override fun onDestory() {
        super.onDestory()
        jobc?.cancel()
        jobPlay?.cancel()
        musicPlay.get().onDestroy()
    }

    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (MEDIA_ID_ROOT == parentId) {
            logE("onLoadChildren parentId333: $parentId")
            appViewModel.get().isInitSuccess.observe(this) {
                it.takeIf {
                    it == true
                }?.let {
                    appViewModel.get().liveData.observe(this@MusicFactory) { list ->
                        serviceScope.launch {
//                            if (isSendChild) {
//                                logE("notifyChildrenChanged $parentId")
//                                isSendChild = false
////                                musicService.notifyChildrenChanged(parentId)
////                                return@launch
//                            }
//                            isSendChild = true
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
                            } catch (e: Exception) {
                                logE("notifyChildrenChanged $parentId   ${e.message}")
                                musicService.notifyChildrenChanged(parentId)
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

    override fun playNext() {
        appViewModel.get().playNext()
    }

    override fun playPrevious() {
        appViewModel.get().playPrevious()
    }

}