package com.wgllss.ssmusic.features_ui.page.playing.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wgllss.core.adapter.BasePagerAdapter
import com.wgllss.core.ex.dpToPx
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.dynamic.lrclibrary.LrcView
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentPlayBinding
import com.wgllss.ssmusic.features_system.music.extensions.albumArtUri
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.extensions.title
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.ExoPlayerUtils.timestampToMSS
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_ui.page.playing.viewmodels.PlayModel
import javax.inject.Inject

class PlayFragment @Inject constructor() : BaseMVVMFragment<PlayModel, FragmentPlayBinding>(R.layout.fragment_play) {

    private val views by lazy { mutableListOf<View>() }
    private var cdAnimator: ValueAnimator? = null
    private var pointAnimator: ValueAnimator? = null
    private val lin by lazy { LinearInterpolator() }

    lateinit var iv_center: ImageView
    lateinit var iv_point: View
    lateinit var cd_layout: View
    lateinit var lrcView: LrcView

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
        binding.sbProgress.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    viewModel.seek(seekBar.progress.toLong())
                    lrcView.updateTime(seekBar.progress.toLong())
                }
            })
        binding.imgBack.setOnClickListener {
            activity?.let { it.finishActivity() }
        }
        initPointAnimat()
        initCDAnimat()

        viewModel.nowPlaying.observe(viewLifecycleOwner) {
            it.id?.let { id ->
                lrcView.loadLrc(LrcHelp.getLrc(id).ifEmpty { "暂无歌词" })
            }
            binding.materMusicName.text = it!!.title
            iv_center.loadUrl(it.albumArtUri)
            Glide.with(this).asBitmap()
                .load(it.albumArtUri)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        resource?.let { it ->
                            Palette.from(it).generate { p ->
                                p?.lightMutedSwatch?.let { s ->
                                    binding.layoutPlayBg.setBackgroundColor(s.rgb)
                                    binding.materMusicName.setTextColor(s.titleTextColor)
//                                    lrcView?.setCurrentColor(p.getMutedColor(s.titleTextColor))
                                    lrcView?.setNormalColor(s.bodyTextColor)
                                    binding.tvTotalTime.setTextColor(s.bodyTextColor)
                                    binding.tvCurrentTime.setTextColor(s.bodyTextColor)
                                }
                            }
                        }
                    }
                })
        }

        viewModel.playbackState.observe(viewLifecycleOwner) {
            when (it.state) {
                STATE_BUFFERING -> {
                    binding.pbLoad.visibility = View.VISIBLE
                    binding.ivPlay.visibility = View.GONE
                }
                STATE_PLAYING -> {
                    binding.pbLoad.visibility = View.GONE
                    binding.ivPlay.visibility = View.VISIBLE
                    if (iv_point.rotation == -40f) {
                        binding.ivPlay.isSelected = true
                        viewModel.isPlaying = true
                        startPointAnimat(-40f, 0f)
                    }
                    it.extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.let { d ->
                        binding.tvTotalTime.text = timestampToMSS(requireContext(), d)
                        binding.sbProgress.max = d.toInt()
                    }
                }
                STATE_PAUSED -> {
                    binding.pbLoad.visibility = View.GONE
                    binding.ivPlay.visibility = View.VISIBLE
                    binding.ivPlay.isSelected = false
                    viewModel.isPlaying = false
                    if (iv_point.rotation == 0f) {
                        startPointAnimat(0f, -40f)
                    }
                }
            }
        }
        viewModel.mediaPosition.observe(viewLifecycleOwner) {
            lrcView.updateTime(it)
            binding.sbProgress.progress = it.toInt()
            binding.tvCurrentTime.text = timestampToMSS(requireContext(), it)
        }
        viewModel.currentPlayMode.observe(viewLifecycleOwner) {
            binding.ivMode.setImageLevel(it)
        }
        viewModel.start()
    }

    override fun onDetach() {
        super.onDetach()
        releaseAnimator()
    }

    private fun initViewPage() {
        val coverView: View = LayoutInflater.from(context).inflate(R.layout.music_cd_layout, null)
        iv_point = coverView.findViewById(R.id.iv_point)
        cd_layout = coverView.findViewById(R.id.cd_layout)
        iv_center = coverView.findViewById(R.id.iv_center)
        iv_point.rotation = -40f
        val lrcLayout: View = LayoutInflater.from(context).inflate(R.layout.lrclayout, null)
        lrcView = lrcLayout.findViewById(R.id.lrc_view)
        views.add(coverView)
        views.add(lrcLayout)
        val pagerAdapter = BasePagerAdapter(views)
        binding.viewPager.adapter = pagerAdapter

        // 加载歌词文本
        lrcView.setDraggable(true, object : LrcView.OnPlayClickListener {
            override fun onPlayClick(view: LrcView?, time: Long): Boolean {
                viewModel.seek(time)
                return true
            }
        })
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
            addUpdateListener { animation ->
                val current = animation.animatedValue as Float
//                setCdRodio(current)  todo
                cd_layout.setRotation(current)
            }
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

    private fun releaseAnimator() {
        cdAnimator?.removeAllUpdateListeners()
        cdAnimator?.removeAllListeners()
        pauseCDanimat()
        cdAnimator = null
        pointAnimator?.removeAllUpdateListeners()
        pointAnimator?.removeAllListeners()
        pointAnimator?.cancel()
        pointAnimator = null
    }
}