package com.wgllss.ssmusic.features_ui.page.home.fragment

import com.scclzkj.base_core.base.BaseMVVMFragment
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentHomeBinding
import com.wgllss.ssmusic.databinding.FragmentSearchBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
@FragmentDestination(pageUrl = "fmt_search", label = "搜索", iconId = R.drawable.ic_dashboard_black_24dp)
class SearchFragment : BaseMVVMFragment<HomeViewModel, FragmentSearchBinding>(R.layout.fragment_search) {
    override fun activitySameViewModel() = true
}