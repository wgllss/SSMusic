package com.wgllss.ssmusic.features_ui.page.locker.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.ex.logE
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentLockerBinding
import com.wgllss.ssmusic.features_system.music.extensions.albumArtUri
import com.wgllss.ssmusic.features_system.music.extensions.artist
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.extensions.title
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.ExoPlayerUtils
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_ui.page.playing.viewmodels.PlayModel
import javax.inject.Inject

class LockerFragment @Inject constructor() : BaseMVVMFragment<PlayModel, FragmentLockerBinding>(R.layout.fragment_locker) {

    override fun activitySameViewModel() = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            playModel = viewModel
            lifecycleOwner = this@LockerFragment
            executePendingBindings()
        }

        viewModel.nowPlaying.observe(viewLifecycleOwner) {
            it?.let {
                it.id?.let { id ->
                    LrcHelp.getLrc(id)?.takeIf { l ->
                        l.isNotEmpty()
                    }?.let { lrc ->
                        binding.lrcView.loadLrc(lrc)
//                        binding   lrcView.loadLrc(lrc)
                    }
                }
                binding.materMusicName.text = it.title
                binding.musicAutor.text = it.artist
                binding.ivCenter.loadUrl(it.albumArtUri)
                Glide.with(this).asBitmap().load(it.albumArtUri).into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        resource?.let { it ->
                            Palette.from(it).generate { p ->
                                p?.lightMutedSwatch?.let { s ->
                                    binding.mainUI.setBackgroundColor(s.rgb)
                                    binding.materMusicName.setTextColor(s.titleTextColor)
                                    binding.musicAutor.setTextColor(s.titleTextColor)
                                    binding.txtDate.setTextColor(s.bodyTextColor)
                                    binding.txtTime.setTextColor(s.bodyTextColor)
                                    binding.txtWeek.setTextColor(s.bodyTextColor)
                                    binding.txtButtom.setTextColor(s.bodyTextColor)
                                    binding.lrcView?.setCurrentColor(p.getMutedColor(s.titleTextColor))
                                    binding.lrcView?.setNormalColor(s.bodyTextColor)
                                }
                            }
                        }
                    }
                })
            }
        }

        viewModel.playbackState.observe(viewLifecycleOwner) {
            when (it.state) {
                PlaybackStateCompat.STATE_PAUSED -> {
                    binding.ivPlay.isSelected = false
                    viewModel.isPlaying = false
                }
                PlaybackStateCompat.STATE_PLAYING -> {
                    binding.ivPlay.isSelected = true
                    viewModel.isPlaying = true
                }
            }
        }
        viewModel.mediaPosition.observe(viewLifecycleOwner) {
            binding.lrcView.updateTime(it)
        }
        viewModel.start()
    }
}