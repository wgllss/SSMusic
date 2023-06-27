package com.wgllss.ssmusic.features_ui.playing.music_widget

import android.content.res.Resources
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.units.WLog
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.features_system.music.extensions.albumArtUri
import com.wgllss.ssmusic.features_system.music.extensions.artist
import com.wgllss.ssmusic.features_system.music.extensions.title

class PlayBarPanel(
    private val play_bar_cover: ImageView,
    private val play_bar_music_name: TextView,
    private val play_bar_author: TextView,
    private val play_bar_list: ImageView,
    private val play_bar_next: ImageView,
    private val play_bar_playOrPause: ImageView,
    private val resources: Resources
) {

    private fun initValue(it: MediaMetadataCompat) {
        play_bar_cover.loadUrl(it.albumArtUri)
        play_bar_music_name.text = it.title?.trim()
        play_bar_author.text = it.artist
        play_bar_list.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_list_36))
        play_bar_next.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_skip_next_36))
        play_bar_playOrPause.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_36))
    }

    fun observe(nowPlaying: MutableLiveData<MediaMetadataCompat>, playbackState: MutableLiveData<PlaybackStateCompat>, owner: LifecycleOwner) {
        nowPlaying.observe(owner) {
            initValue(it)
        }
        playbackState.observe(owner) {
            WLog.e(this, "status : ${it.state}")
            when (it.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    play_bar_playOrPause.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_36))
                }
                else -> {
                    play_bar_playOrPause.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow_36))
                }
            }
        }
    }
}