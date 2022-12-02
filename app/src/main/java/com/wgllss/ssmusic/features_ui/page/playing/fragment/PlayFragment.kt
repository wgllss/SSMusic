package com.wgllss.ssmusic.features_ui.page.playing.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.BasePagerAdapter
import com.wgllss.ssmusic.core.ex.dpToPx
import com.wgllss.ssmusic.core.ex.finishActivity
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.databinding.FragmentPlayBinding
import com.wgllss.ssmusic.features_system.music.extensions.*
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.ExoPlayerUtils
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.ExoPlayerUtils.timestampToMSS
import com.wgllss.ssmusic.features_system.music.impl.wlmusicplayer.WlTimeUtil
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.PlayModel
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        sb_progress.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    viewModel.seek(seekBar.progress.toLong())
                }
            })
        img_back.setOnClickListener {
            activity?.let { it.finishActivity() }
        }
        initPointAnimat()
        initCDAnimat()

        viewModel.nowPlaying.observe(viewLifecycleOwner) {
            mater_music_name.text = it!!.title
            sb_progress.max = it.duration.toInt()
            logE("it.duration：${it.duration}")
            tv_total_time.text = timestampToMSS(requireContext(), it.duration)
            iv_center.loadUrl(it.albumArtUri)
        }

        viewModel.playbackState.observe(viewLifecycleOwner) {
            when (it.state) {
                STATE_BUFFERING -> {
                    pb_load.visibility = View.VISIBLE
                    iv_play.visibility = View.GONE
                }
                STATE_PLAYING -> {
                    pb_load.visibility = View.GONE
                    iv_play.visibility = View.VISIBLE
                    if (iv_point.rotation == -40f) {
                        iv_play.isSelected = true
                        viewModel.isPlaying = true
                        startPointAnimat(-40f, 0f)
                    }
                    if (it.extras != null) {
                        logE("it.extras: ${it.extras}")
                    }
                    it.extras?.getLong("duration")?.let { d ->
                        tv_total_time.text = timestampToMSS(requireContext(), d)
                        sb_progress.max = d.toInt()
                    }
                }
                STATE_PAUSED -> {
                    iv_play.isSelected = false
                    viewModel.isPlaying = false
                    if (iv_point.rotation == 0f) {
                        startPointAnimat(0f, -40f)
                    }
                }
            }
        }
        viewModel.mediaPosition.observe(viewLifecycleOwner) {
            sb_progress.progress = it.toInt()
            tv_current_time.text = timestampToMSS(requireContext(), it)
        }
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
            repeatCount = 0
            duration = 300
            interpolator = lin
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