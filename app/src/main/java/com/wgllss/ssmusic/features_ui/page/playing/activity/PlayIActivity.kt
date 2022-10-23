package com.wgllss.ssmusic.features_ui.page.playing.activity

import android.os.Bundle
import com.scclzkj.base_core.extension.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.databinding.ActivityPlayBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.PlayModel
import com.wgllss.ssmusic.features_ui.page.playing.fragment.PlayFragment
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayActivity : BaseMVVMActivity<PlayModel, ActivityPlayBinding>(R.layout.activity_play) {

    @Inject
    lateinit var playFragmentL: Lazy<PlayFragment>

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setFramgment(playFragmentL.get(), R.id.content)
    }

}