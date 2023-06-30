package com.wgllss.ssmusic.features_ui.page.playlist.activity

import android.os.Bundle
import com.wgllss.core.activity.BaseMVVMActivity
import com.wgllss.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.ActivityPlayBinding
import com.wgllss.ssmusic.features_ui.page.playlist.fragment.HistoryFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayListActivity : BaseMVVMActivity<HomeViewModel2, ActivityPlayBinding>(R.layout.activity_play) {
    @Inject
    lateinit var historyFragmentL: Lazy<HistoryFragment>

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setFramgment(historyFragmentL.get(), R.id.content)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }
}