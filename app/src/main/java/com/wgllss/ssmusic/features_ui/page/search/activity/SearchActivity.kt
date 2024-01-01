package com.wgllss.ssmusic.features_ui.page.search.activity

import android.os.Bundle
import com.wgllss.core.activity.BaseMVVMActivity
import com.wgllss.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.ActivityPlayBinding
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.search.fragment.SearchTabFragment
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BaseMVVMActivity<HomeViewModel, ActivityPlayBinding>(R.layout.activity_play) {

    @Inject
    lateinit var searchTabFragmentL: Lazy<SearchTabFragment>

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setFramgment(searchTabFragmentL.get(), R.id.content)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }

    override fun initValue() {
        super.initValue()
        viewModel.lazyTabView()
    }
}