package com.wgllss.ssmusic.features_ui.page.playing.fragment

import android.os.Bundle
import android.widget.SeekBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.databinding.FragmentPlayBinding
import com.wgllss.ssmusic.features_system.music.impl.wlmusicplayer.WlTimeUtil
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
                    sb_progress.progress = WlTimeUtil.getProgress(it.currSecs, it.totalSecs)
                    tv_current_time.text = WlTimeUtil.secdsToDateFormat(it.currSecs, it.totalSecs)
                    tv_total_time.text = WlTimeUtil.secdsToDateFormat(it.totalSecs, it.totalSecs)
                    viewModel.toatal?.takeIf { t ->
                        t.value == null || t.value != it.totalSecs
                    }?.let { _ ->
                        viewModel.toatal.postValue(it.totalSecs)
                    }
                }
                is MusicEvent.BufferingUpdate -> {
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

        viewModel.position.observe(viewLifecycleOwner) {
            logE("position:${it}")
        }

        sb_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel?.toatal?.value?.run {
                    val position = this * progress / 100
                    viewModel.position.postValue(position)
                    tv_current_time.text = WlTimeUtil.secdsToDateFormat(position, this)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.seek(false, false)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                logE("onStopTrackingTouch ")
                viewModel.seek(true, true)
            }
        })
    }

}