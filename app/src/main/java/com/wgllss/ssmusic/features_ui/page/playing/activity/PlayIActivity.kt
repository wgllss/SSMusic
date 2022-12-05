package com.wgllss.ssmusic.features_ui.page.playing.activity

import android.content.Intent
import android.os.Bundle
import com.wgllss.ssmusic.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.ActivityManager
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.databinding.ActivityPlayBinding
import com.wgllss.ssmusic.features_ui.page.playing.viewmodels.PlayModel
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logE("onNewIntent")
    }

    override fun lazyInitValue() {
        super.lazyInitValue()
        ActivityManager.instance.print()
    }

}