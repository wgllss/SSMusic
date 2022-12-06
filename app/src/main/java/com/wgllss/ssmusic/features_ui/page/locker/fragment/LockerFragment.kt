package com.wgllss.ssmusic.features_ui.page.locker.fragment

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.loadUrl
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
                binding.ivCenter.loadUrl(it.albumArtUri)
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
    }
}