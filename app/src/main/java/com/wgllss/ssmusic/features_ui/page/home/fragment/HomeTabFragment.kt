package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.ViewPage2ChildFragmentAdapter
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.fragment.BaseFragment
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.databinding.FragmentHomeTabBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import javax.inject.Inject

class HomeTabFragment @Inject constructor() : BaseFragment(R.layout.fragment_home_tab) {

    private lateinit var childAdapter: ViewPage2ChildFragmentAdapter
    private lateinit var homeTabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "onCreateView")
        val view = LayoutContains.getViewByKey(inflater.context, LaunchInflateKey.home_tab_fragment)!!
        homeTabLayout = view.findViewById(inflater.context.resources.getIdentifier("homeTabLayout", "id", inflater.context.packageName))
        viewPager2 = view.findViewById(inflater.context.resources.getIdentifier("homeViewPager2", "id", inflater.context.packageName))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogTimer.LogE(this, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        childAdapter = ViewPage2ChildFragmentAdapter(getList(), childFragmentManager, lifecycle)
        viewPager2.adapter = childAdapter
        TabLayoutMediator(homeTabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            tab.text = (childAdapter.list[position] as HomeFragment).title
        }.apply(TabLayoutMediator::attach)
        LogTimer.LogE(this, "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        LogTimer.LogE(this, "onResume")
    }

    private fun getList() = mutableListOf(
        HomeFragment("首页", "index"),
        HomeFragment("华语", "forum-1"),
        HomeFragment("日韩", "forum-15"),
        HomeFragment("欧美", "forum-10"),
        HomeFragment("remix", "thread-21683"),
        HomeFragment("纯音乐", "forum-12"),
        HomeFragment("异次元", "forum-13"),
    )
}