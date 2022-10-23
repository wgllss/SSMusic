package com.wgllss.ssmusic.features_system.music.impl.mediaplayer

import android.content.Context
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext

class AudioFocusManager constructor(@ApplicationContext val context: Context, val mediaPlayerImpl: MediaPlayerImpl) : AudioManager.OnAudioFocusChangeListener {
    private var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var isPausedByFocusLossTransient = false

    fun requestAudioFocus(): Boolean {
        return (audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
    }

    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    mediaPlayerImpl.stopPlayer()
                }

                // 恢复音量
                mediaPlayerImpl.setVolume(100)
                isPausedByFocusLossTransient = false
            }
            AudioManager.AUDIOFOCUS_LOSS -> mediaPlayerImpl.pausePlayer()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayerImpl.pausePlayer(false)
                isPausedByFocusLossTransient = true
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->                 // 音量减小为一半
                mediaPlayerImpl.setVolume(50)
        }
    }
}