package com.wgllss.ssmusic.features_ui.page.mv.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wgllss.core.activity.BaseViewModelActivity
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.getIntToDip
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.page.mv.viewmodel.MVViewModel

class KMVActivity : BaseViewModelActivity<MVViewModel>() {
    private var player: ExoPlayer? = null

    private lateinit var playerView: StyledPlayerView
    private lateinit var img_back: ImageView
    private lateinit var layout_title: FrameLayout
    private lateinit var title: TextView
    private var startItemIndex: Int = 0
    private var startPosition: Long = 0

    private var url = ""

    companion object {
        private const val KEY_ITEM_INDEX = "item_index"
        private const val KEY_POSITION = "position"
        private const val KEY_MV = "KEY_MV"
        private const val KEY_MV_TITE = "KEY_MV_TITE"

        fun startKMVActivity(context: Context, url: String, title: String) {
            context.startActivity(Intent(context, KMVActivity::class.java).apply {
                putExtra(KEY_MV, url)
                putExtra(KEY_MV_TITE, title)
            })
        }
    }

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setContentView(R.layout.activity_kmv)
        playerView = findViewById(R.id.player_view)
        layout_title = findViewById(R.id.layout_title)
        img_back = findViewById(R.id.img_back)
        title = findViewById(R.id.title)
        playerView.requestFocus()
        if (savedInstanceState != null) {
            startItemIndex = savedInstanceState.getInt(KEY_ITEM_INDEX)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        }
    }

    override fun initValue() {
        super.initValue()
        url = intent?.getStringExtra(KEY_MV) ?: ""
        title.text = intent?.getStringExtra(KEY_MV_TITE) ?: ""
        playerView.setShowFastForwardButton(false)
        playerView.setShowNextButton(false)
        playerView.setShowPreviousButton(false)
        playerView.setShowRewindButton(false)

        playerView.setFullscreenButtonClickListener {
            requestedOrientation = if (it) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        playerView.setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layout_title.layoutParams.height = this@KMVActivity.getIntToDip(45f).toInt()
                log("横屏")
            }
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                layout_title.layoutParams.height = this@KMVActivity.getIntToDip(81f).toInt()
                log("竖屏")
            }
            layout_title.visibility = it
        })
        img_back.setOnClickListener {
            finishActivity()
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this)
//        val adaptiveMimeType = Util.getAdaptiveMimeTypeForContentType(
//            Util.inferContentType(uri)
//        )
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
    }


    private fun initializePlayer(): Boolean {
        if (player == null) {
            val playerBuilder = ExoPlayer.Builder( /* context= */this)
                .setMediaSourceFactory(DefaultMediaSourceFactory(/* context= */ this))
                .setRenderersFactory(DefaultRenderersFactory(this))
                .setLoadControl(DefaultLoadControl())
            player = playerBuilder.build()
            player?.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
            player?.playWhenReady = true
            playerView.player = player
        }
        android.util.Log.e("MainActivity", "initializePlayer----startItemIndex:$startItemIndex startPosition:$startPosition")
        val playUri: Uri = Uri.parse(url)
        val mediaSource: MediaSource = buildMediaSource(playUri)
        player?.prepare(mediaSource, true, false)
        player?.seekTo(startPosition)
        return true
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        android.util.Log.e("MainActivity", "onNewIntent")
//        releasePlayer()
////        releaseClientSideAdsLoader()
////        clearStartPosition()
//        setIntent(intent)
//    }

    override fun onStart() {
        super.onStart()
        android.util.Log.e("MainActivity", "onStart")
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.e("MainActivity", "onResume")
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.e("MainActivity", "onPause")
        if (Build.VERSION.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause()
            }
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        android.util.Log.e("MainActivity", "onStop")
        if (Build.VERSION.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause()
            }
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {

            log("releasePlayer if (player != null) {")
//            updateTrackSelectorParameters()
            updateStartPosition()
//            releaseServerSideAdsLoader()
//            debugViewHelper.stop()
//            debugViewHelper = null
            player?.release()
            player = null
            playerView.player = null
//            mediaItems = emptyList<MediaItem>()
        }
//        if (clientSideAdsLoader != null) {
//            clientSideAdsLoader.setPlayer(null)
//        } else {
        playerView.adViewGroup.removeAllViews()
//        }
    }

    private fun updateStartPosition() {
        if (player != null) {
//            startAutoPlay = player!!.playWhenReady
            startItemIndex = player!!.currentMediaItemIndex
            log("log(player!!.contentPosition)--${player?.contentPosition}")
            startPosition = Math.max(0, player!!.contentPosition)

            android.util.Log.e("MainActivity", "startItemIndex:$startItemIndex startPosition:$startPosition")
        }
    }

    private fun log(message: String) {
        android.util.Log.e("MainActivity", message)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        android.util.Log.e("MainActivity", "onSaveInstanceState")
//        updateTrackSelectorParameters()
        updateStartPosition()
//        outState.putBundle(com.google.android.exoplayer2.demo.PlayerActivity.KEY_TRACK_SELECTION_PARAMETERS, trackSelectionParameters.toBundle())
//        outState.putBoolean(com.google.android.exoplayer2.demo.PlayerActivity.KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_ITEM_INDEX, startItemIndex)
        outState.putLong(KEY_POSITION, startPosition)
        android.util.Log.e("MainActivity", "startItemIndex:$startItemIndex")
//        saveServerSideAdsLoaderState(outState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        log("    newConfig.orientation ：${newConfig.orientation}")
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (layout_title.visibility == View.VISIBLE)
                layout_title.layoutParams.height = this@KMVActivity.getIntToDip(45f).toInt()
            log("横屏")
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layout_title.visibility == View.VISIBLE)
                layout_title.layoutParams.height = this@KMVActivity.getIntToDip(81f).toInt()
            log("竖屏")
        }
    }
}