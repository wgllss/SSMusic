package com.wgllss.ssmusic.features_ui.page.search.activity

import android.os.Bundle
import com.wgllss.core.activity.BaseMVVMActivity
import com.wgllss.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.ActivityPlayBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2
import com.wgllss.ssmusic.features_ui.page.search.fragment.KSearchFragment
import com.wgllss.ssmusic.features_ui.page.search.fragment.SearchFragment
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BaseMVVMActivity<HomeViewModel2, ActivityPlayBinding>(R.layout.activity_play) {
    @Inject
    lateinit var searchFragmentL: Lazy<SearchFragment>

    @Inject
    lateinit var ksearchFragmentL: Lazy<KSearchFragment>

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        val type = intent?.getIntExtra("SEARCH_TYPE_KEY", 0) ?: 0
        setFramgment(if (type == 0) searchFragmentL.get() else ksearchFragmentL.get(), R.id.content)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }
}