package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.ViewPage2ChildFragmentAdapter
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.databinding.FragmentHomeTabBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@FragmentDestination(pageUrl = "fmt_home", asStarter = true, label = "首页", iconId = R.drawable.ic_home_black_24dp)
class HomeTabFragment : BaseMVVMFragment<HomeViewModel, FragmentHomeTabBinding>(R.layout.fragment_home_tab) {

    private lateinit var childAdapter: ViewPage2ChildFragmentAdapter

    override fun activitySameViewModel() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = AsyncInflateManager.instance.getInflatedView(inflater.context, R.layout.fragment_home_tab, container, LaunchInflateKey.home_tab_fragment, inflater)
        binding = DataBindingUtil.bind(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogTimer.LogE(this, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        childAdapter = ViewPage2ChildFragmentAdapter(getList(), childFragmentManager, lifecycle)
        binding.apply {
            homeViewPager2.adapter = childAdapter
//            homeViewPager2.offscreenPageLimit = 1
            TabLayoutMediator(homeTabLayout, homeViewPager2) { tab: TabLayout.Tab, position: Int ->
                tab.text = (childAdapter.list[position] as HomeFragment).title
            }.apply(TabLayoutMediator::attach)
        }
    }

    private fun getList() = mutableListOf(
        HomeFragment("首页","index"),
        HomeFragment("华语","forum-1"),
        HomeFragment("日韩","forum-15"),
        HomeFragment("欧美","forum-10"),
        HomeFragment("remix","thread-21683"),
        HomeFragment("纯音乐","forum-12"),
        HomeFragment("异次元","forum-13"),
    )
}