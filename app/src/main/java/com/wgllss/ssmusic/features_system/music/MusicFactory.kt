package com.wgllss.ssmusic.features_system.music

import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.data.livedatabus.PlayerEvent
import com.wgllss.ssmusic.dl.annotations.BindExoPlayer
import com.wgllss.ssmusic.dl.annotations.BindWlMusic
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.services.MusicService
import dagger.Lazy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 音乐播放工厂，处理多音乐功能，
 * musicPlay:主持音乐各种操作
 * appViewModel:主持提供各种数据
 */

class MusicFactory @Inject constructor(@BindExoPlayer private val musicPlay: Lazy<IMusicPlay>, private val appViewModel: Lazy<AppViewModel>) : MusicComponent() {

    private lateinit var playerProgress: MusicEvent.PlayerProgress
    private lateinit var playerLoadding: MusicEvent.PlayerLoadding
    private lateinit var playerStart: MusicEvent.PlayerStart
    private lateinit var playerPause: MusicEvent.PlayerPause
    private lateinit var intentReceiver: IntentReceiver

    private var jobc: Job? = null
    private var jobPlay: Job? = null
    private var currentUrl: String? = null
    private var pause = true
    private var isUItoFront = false
    private var isNewAddToPlaylist = false

    override fun isPlaying() = musicPlay.get().isPlaying() && !pause

    override fun onCreate(musicService: MusicService) {
        super.onCreate(musicService)
        jobc = GlobalScope.launch {
            musicPlay.get().onCreate()
            LiveEventBus.get(MusicBeanEvent::class.java).observeForever {
//                jobPlay = GlobalScope.launch {
                onMusicDo(it)
//                }
            }
            LiveEventBus.get(PlayerEvent::class.java).observeForever {
                when (it) {
                    is PlayerEvent.PlayEvent -> {
                        if (it.pause) musicPlay.get().onPause()
                        else musicPlay.get().onResume()
                    }
                    is PlayerEvent.PlayNext -> {
                        appViewModel.get().playNext()
                    }
                    is PlayerEvent.PlayPrevious -> {
                        appViewModel.get().playPrevious()
                    }
                    is PlayerEvent.SeekEvent -> {
                        musicPlay.get().seek(it.position, it.seekingfinished, it.showTime)
                    }
                    is PlayerEvent.PlayUIToFront -> {
                        isUItoFront = it.isFront
                        logE("is PlayerEvent.PlayUIToFront isUItoFront $isUItoFront")
                        if (isUItoFront) {
                            if (isPlaying()) {
                                LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.ChangeMusic(musicPic, musicTitle, musicAuthor))
                            }
                            if (!this@MusicFactory::playerStart.isInitialized) {
                                playerStart = MusicEvent.PlayerStart
                            }
                            if (!this@MusicFactory::playerPause.isInitialized) {
                                playerPause = MusicEvent.PlayerPause
                            }
                            LiveEventBus.get(MusicEvent::class.java).post(if (pause) playerStart else playerPause)
                        }
                    }
                    else -> {

                    }
                }
            }
            // Initialize the intent filter and each action
            val filter = IntentFilter()
//            filter.addAction(SERVICECMD)
            filter.addAction(TOGGLEPAUSE_ACTION)
//            filter.addAction(PAUSE_ACTION)
//            filter.addAction(STOP_ACTION)
            filter.addAction(NEXT_ACTION)
            filter.addAction(PREVIOUS_ACTION)
//            filter.addAction(PREVIOUS_FORCE_ACTION)
//            filter.addAction(REPEAT_ACTION)
//            filter.addAction(SHUFFLE_ACTION)
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            filter.addAction(Intent.ACTION_SCREEN_ON)
            if (!this@MusicFactory::intentReceiver.isInitialized) {
                intentReceiver = IntentReceiver()
            }
            musicService.registerReceiver(intentReceiver, filter)

        }
        appViewModel.get().currentPosition.observeForever {
            if (!isNewAddToPlaylist)
                appViewModel.get().getDetail(it)
            isNewAddToPlaylist = false
        }
    }

    override fun onDestory() {
        super.onDestory()
        jobc?.cancel()
        jobPlay?.cancel()
        musicPlay.get().onDestroy()
        musicService?.unregisterReceiver(intentReceiver)
    }

    //处理音乐播放
    fun onMusicDo(it: MusicBeanEvent) {
        it.run {
            if (currentUrl.isNullOrEmpty()) {
                currentUrl = url
                currentUrl?.let {
                    musicPlay.get().apply {
                        setSource(it)
                        setVolume(100)
                        prePared()
                        setOnPreparedListener(object : OnPreparedListener {
                            override fun onPrepared() {
                                musicPlay.get().start()
                                logE("onPrepared")
                            }
                        })
                        setOnCompleteListener(object : OnPlayCompleteListener {
                            override fun onComplete() {
                                isNewAddToPlaylist = false
                                appViewModel.get().playNext()
                            }
                        })
                        setOnLoadListener(object : OnLoadListener {
                            override fun onLoad(load: Boolean) {
                                logE("onLoad: $load")
                                if (!load) {
                                    pause = load
                                    updateNotification()
                                }
                                if (!this@MusicFactory::playerLoadding.isInitialized) {
                                    playerLoadding = MusicEvent.PlayerLoadding(load)
                                } else {
                                    playerLoadding.loadding = load
                                }
                                LiveEventBus.get(MusicEvent::class.java).post(playerLoadding)
                            }
                        })
                        setOnPlayInfoListener(object : OnPlayInfoListener {
                            override fun onPlayInfo(currSecs: Int, totalSecs: Int) {
                                if (!isUItoFront) {
                                    return
                                }
                                if (totalSecs > 0) {
                                    if (!this@MusicFactory::playerProgress.isInitialized) {
                                        playerProgress = MusicEvent.PlayerProgress(currSecs, totalSecs)
                                    } else {
                                        playerProgress!!.totalSecs = totalSecs
                                        playerProgress!!.currSecs = currSecs
                                    }
                                    LiveEventBus.get(MusicEvent::class.java).post(playerProgress)
                                }
                            }
                        })
                        setOnPauseResumeListener(object : OnPauseResumeListener {
                            override fun onPause(pause: Boolean) {
                                logE("pause: $pause")
                                this@MusicFactory.pause = pause
                                if (pause) mLastPlayedTime = System.currentTimeMillis()
                                updateNotification()
                                if (!this@MusicFactory::playerStart.isInitialized) {
                                    playerStart = MusicEvent.PlayerStart
                                }
                                if (!this@MusicFactory::playerPause.isInitialized) {
                                    playerPause = MusicEvent.PlayerPause
                                }
                                LiveEventBus.get(MusicEvent::class.java).post(if (pause) playerStart else playerPause)
                            }
                        })
                    }
                }
                isNewAddToPlaylist = true
                appViewModel.get().addToPlayList(it)
            } else {
                if (currentUrl == url) {
                    if (musicPlay.get().isPlaying()) {
                        return@run
                    } else {
                        musicPlay.get().playNext(url)
                    }
                } else {
                    currentUrl = url
                    currentUrl?.run {
                        musicPlay.get().playNext(this)
                    }
                    appViewModel.get().addToPlayList(it)
                }
            }
            musicTitle = title
            musicAuthor = author
            musicPic = pic
            it.run {
                LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.ChangeMusic(pic, title, author))
            }
        }
    }

    override fun handleCommandIntent(intent: Intent?) {
        intent?.action?.run {
            logE(" intent?.action ${intent?.action}")
            when (this) {
                PREVIOUS_ACTION -> {
                    appViewModel.get().playPrevious()
                }
                TOGGLEPAUSE_ACTION -> {
                    if (pause) musicPlay.get().onResume() else musicPlay.get().onPause()
                }
                NEXT_ACTION -> {
                    appViewModel.get().playNext()
                }
                else -> {

                }
            }
        }
    }

}