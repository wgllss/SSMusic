package com.wgllss.ssmusic.features_ui.page.classics.activity

import android.os.Bundle
import com.wgllss.core.activity.BaseMVVMActivity
import com.wgllss.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.ActivityPlayBinding
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.classics.fragment.HomeTabFragment

//@AndroidEntryPoint
class ClassicsActivity : BaseMVVMActivity<HomeViewModel, ActivityPlayBinding>(R.layout.activity_play) {
    val homeTabFragment by lazy { HomeTabFragment() }

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setFramgment(homeTabFragment, R.id.content)
    }

    override fun initValue() {
        super.initValue()
        viewModel.lazyTabView()
    }
}