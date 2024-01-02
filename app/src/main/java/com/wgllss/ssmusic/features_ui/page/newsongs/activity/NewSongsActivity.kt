package com.wgllss.ssmusic.features_ui.page.newsongs.activity

import android.os.Bundle
import com.wgllss.core.activity.BaseViewModelActivity
import com.wgllss.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.newsongs.fragment.NewSongsFragment

class NewSongsActivity : BaseViewModelActivity<HomeViewModel>() {

    private val fragment by lazy { NewSongsFragment() }

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setContentView(R.layout.activity_new_songs)
        setFramgment(fragment, R.id.content)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }

    override fun initValue() {
        super.initValue()
        viewModel.lazyTabView()
    }
}