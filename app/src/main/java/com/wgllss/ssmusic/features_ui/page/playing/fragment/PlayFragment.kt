package com.wgllss.ssmusic.features_ui.page.playing.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.scclzkj.base_core.extension.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.BasePagerAdapter
import com.wgllss.ssmusic.core.ex.dpToPx
import com.wgllss.ssmusic.core.ex.finishActivity
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.databinding.FragmentPlayBinding
import com.wgllss.ssmusic.features_system.music.impl.wlmusicplayer.WlTimeUtil
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.PlayModel
import kotlinx.android.synthetic.main.fragment_play.*
import javax.inject.Inject

class PlayFragment @Inject constructor() : BaseMVVMFragment<PlayModel, FragmentPlayBinding>(R.layout.fragment_play) {

    private val views by lazy { mutableListOf<View>() }
    private var cdAnimator: ValueAnimator? = null
    private var pointAnimator: ValueAnimator? = null
    private val lin by lazy { LinearInterpolator() }

    lateinit var iv_center: ImageView
    lateinit var iv_point: View
    lateinit var cd_layout: View

    override fun activitySameViewModel() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPage()
    }

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
                    viewModel.pic.postValue(it.pic)
                }
                is MusicEvent.PlayerProgress -> {
                    sb_progress.progress = WlTimeUtil.getProgress(it.currSecs, it.totalSecs)
                    tv_current_time.text = WlTimeUtil.secdsToDateFormat(it.currSecs, it.totalSecs)
                    if (pb_load.visibility == View.VISIBLE) {
                        pb_load.visibility = View.GONE
                    }
                    if (iv_play.visibility == View.GONE) {
                        iv_play.visibility = View.VISIBLE
                    }
                    if (iv_point.rotation == -40f) {
                        iv_play.isSelected = true
                        viewModel.isPlaying = true
                        startPointAnimat(-40f, 0f)
                    }

                    viewModel.toatal?.takeIf { t ->
                        t.value == null || t.value != it.totalSecs
                    }?.let { _ ->
                        viewModel.toatal.postValue(it.totalSecs)
                        tv_total_time.text = WlTimeUtil.secdsToDateFormat(it.totalSecs, it.totalSecs)
                    }
                }
                is MusicEvent.BufferingUpdate -> {
                    sb_progress.secondaryProgress = (sb_progress.max * 100 / it.percent)
                }
                is MusicEvent.PlayerStart -> {// false 显示播放UI
                    iv_play.isSelected = false
                    viewModel.isPlaying = false
                    if (iv_point.getRotation() == 0f) {
                        startPointAnimat(0f, -40f)
                    }
                }

                is MusicEvent.PlayerPause -> {// true 显示暂停
                    iv_play.isSelected = true
                    viewModel.isPlaying = true
                    if (iv_point.getRotation() == -40f) {
                        startPointAnimat(-40f, 0f)
                    }
                }
                is MusicEvent.PlayerLoadding -> {
                    pb_load.visibility = if (it.loadding) View.VISIBLE else View.GONE
                    iv_play.visibility = if (it.loadding) View.GONE else View.VISIBLE
                }
                else -> {

                }
            }
        }

        sb_progress.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
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
                    viewModel.seek(true, true)
                }
            })
        img_back.setOnClickListener {
            activity?.let { it.finishActivity() }
        }
        iv_center?.run {
            viewModel.pic.observe(viewLifecycleOwner) {
                iv_center.loadUrl(it)
            }
        }
        initPointAnimat()
        initCDAnimat()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }


    fun initViewPage() {
        val coverView: View = LayoutInflater.from(context).inflate(R.layout.music_cd_layout, null)
        iv_point = coverView.findViewById(R.id.iv_point)
        cd_layout = coverView.findViewById(R.id.cd_layout)
        iv_center = coverView.findViewById(R.id.iv_center)
        iv_point.rotation = -40f
        views.add(coverView)
        val pagerAdapter = BasePagerAdapter(views)
        view_pager.adapter = pagerAdapter
    }

    /**
     * 初始化指针动画
     */
    private fun initPointAnimat() {
        iv_point.setPivotX(requireContext().dpToPx(17.0f))
        iv_point.setPivotY(requireContext().dpToPx(15.0f))
        pointAnimator = ValueAnimator.ofFloat(0f, 0f)
        pointAnimator?.apply {
            setTarget(iv_point)
            setRepeatCount(0)
            setDuration(300)
            setInterpolator(lin)
            addUpdateListener { animation ->
                val current = animation.animatedValue as Float
                iv_point.setRotation(current)
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    if (!viewModel.isPlaying) {
                        pauseCDanimat()
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (viewModel.isPlaying) {
                        resumeCDanimat()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    /**
     * 开始指针动画
     *
     * @param from
     * @param end
     */
    private fun startPointAnimat(from: Float, end: Float) {
        if (pointAnimator != null) {
            if (from < end) {
                if (!viewModel.isPlaying) {
                    return
                }
            } else {
                if (viewModel.isPlaying) {
                    return
                }
            }
            pointAnimator!!.setFloatValues(from, end)
            pointAnimator!!.start()
        }
    }

    /**
     * 初始化CD动画
     */
    private fun initCDAnimat() {
        cdAnimator = ValueAnimator.ofFloat(cd_layout.getRotation(), 360f + cd_layout.getRotation())
        cdAnimator?.apply {
            setTarget(cd_layout)
            setRepeatCount(ValueAnimator.INFINITE)
            setDuration(15000)
            setInterpolator(lin)
            addUpdateListener(AnimatorUpdateListener { animation ->
                val current = animation.animatedValue as Float
//                setCdRodio(current)  todo
                cd_layout.setRotation(current)
            })
        }
    }

    /**
     * 开始cd动画
     */
    private fun resumeCDanimat() {
        if (cdAnimator != null && !cdAnimator!!.isRunning) {
            cdAnimator!!.setFloatValues(cd_layout.getRotation(), 360f + cd_layout.getRotation())
            cdAnimator!!.start()
        }
    }

    /**
     * 暂停CD动画
     */
    private fun pauseCDanimat() {
        if (cdAnimator != null && cdAnimator!!.isRunning) {
            cdAnimator!!.cancel()
        }
    }
}