package com.wgllss.ssmusic.features_ui.playing.music_widget

import android.content.Intent
import android.content.res.Resources
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.widget.CommonToast
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.features_system.activation.ActivationUtils
import com.wgllss.ssmusic.features_system.music.extensions.albumArtUri
import com.wgllss.ssmusic.features_system.music.extensions.artist
import com.wgllss.ssmusic.features_system.music.extensions.title
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection

class PlayBarPanel(
    private val play_bar_cover: ImageView,
    private val play_bar_music_name: TextView,
    private val play_bar_author: TextView,
    private val play_bar_list: ImageView,
    private val play_bar_next: ImageView,
    private val play_bar_playOrPause: ImageView,
    private val resources: Resources
) {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }

    private fun initValue(it: MediaMetadataCompat) {
        play_bar_cover.loadUrl(it.albumArtUri)
        play_bar_music_name.text = it.title?.trim()
        play_bar_author.text = it.artist
        play_bar_list.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_list_36))
        play_bar_next.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_skip_next_36))
        play_bar_playOrPause.setImageDrawable(resources.getDrawable(R.drawable.play_bar_play_pause_selector))
        play_bar_playOrPause.isSelected = false
        play_bar_playOrPause.setOnClickListener {
            musicServiceConnectionL.transportControls.run {
                if (it.isSelected) pause() else {
                    if (ActivationUtils.isUnUsed()) {
                        CommonToast.show("亲！请您先激活吧")
                        return@run
                    }
                    play()
                }
            }
        }
        play_bar_next.setOnClickListener {
            musicServiceConnectionL.transportControls.skipToNext()
        }
        play_bar_list.setOnClickListener {
            it.context?.run {
                try {
                    val clazz = Class.forName("com.wgllss.ssmusic.features_ui.page.playlist.activity.PlayListActivity")
                    startActivity(Intent(this, clazz))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        (play_bar_cover.parent as View).setOnClickListener {
            it.context?.run {
                try {
                    val clazz = Class.forName("com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity")
                    startActivity(Intent(this, clazz))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun observe(nowPlaying: MutableLiveData<MediaMetadataCompat>, playbackState: MutableLiveData<PlaybackStateCompat>, owner: LifecycleOwner) {
        nowPlaying.observe(owner) {
            initValue(it)
        }
        playbackState.observe(owner) {
            WLog.e(this, "status : ${it.state}")
            when (it.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    play_bar_playOrPause.isSelected = true
                }
                else -> {
                    play_bar_playOrPause.isSelected = false
                }
            }
        }
    }
}