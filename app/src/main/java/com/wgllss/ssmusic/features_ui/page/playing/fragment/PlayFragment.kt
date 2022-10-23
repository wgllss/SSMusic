package com.wgllss.ssmusic.features_ui.page.playing.fragment

import android.os.Bundle
import com.jeremyliao.liveeventbus.LiveEventBus
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.databinding.FragmentPlayBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.PlayModel
import kotlinx.android.synthetic.main.fragment_play.*
import javax.inject.Inject

class PlayFragment @Inject constructor() : BaseMVVMFragment<PlayModel, FragmentPlayBinding>(R.layout.fragment_play) {

    override fun activitySameViewModel() = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            playModel = viewModel
            lifecycleOwner = this@PlayFragment
            executePendingBindings()
        }
        LiveEventBus.get(MusicEvent::class.java).observe(viewLifecycleOwner) {
            when (it) {
                is MusicEvent.ChangeMusic -> {
                    mater_music_name.text = it.title
                }
                is MusicEvent.PlayerProgress -> {
                    logE("it.progress ${it.progress}")
                    sb_progress.progress = it.progress
                }
                is MusicEvent.BufferingUpdate -> {
                    logE("it.percent ${it.percent}")
                    sb_progress.secondaryProgress = (sb_progress.max * 100 / it.percent)
                }
                is MusicEvent.PlayerStart -> {// false 显示播放UI
                    iv_play.isSelected = false
                }

                is MusicEvent.PlayerPause -> {// true 显示暂停
                    iv_play.isSelected = true
                }
                else -> {

                }
            }
        }
    }

}