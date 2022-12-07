package com.wgllss.ssmusic.features_ui.page.locker.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.databinding.FragmentLockerBinding
import com.wgllss.ssmusic.features_system.music.extensions.albumArtUri
import com.wgllss.ssmusic.features_system.music.extensions.artist
import com.wgllss.ssmusic.features_system.music.extensions.title
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
                binding.materMusicName.text = it.title
                binding.musicAutor.text = it.artist
                Glide.with(this).asBitmap().load(it.albumArtUri).into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        resource?.let { it ->
                            binding.ivCenter.setImageBitmap(resource)
                            Palette.from(it).generate { p ->
                                p?.lightMutedSwatch?.let { s ->
                                    binding.mainUI.setBackgroundColor(s.rgb)
                                    binding.materMusicName.setTextColor(s.titleTextColor)
                                    binding.musicAutor.setTextColor(s.titleTextColor)
                                    binding.txtDate.setTextColor(s.bodyTextColor)
                                    binding.txtTime.setTextColor(s.bodyTextColor)
                                    binding.txtWeek.setTextColor(s.bodyTextColor)
                                    binding.txtButtom.setTextColor(s.bodyTextColor)
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
        viewModel.start()
    }
}