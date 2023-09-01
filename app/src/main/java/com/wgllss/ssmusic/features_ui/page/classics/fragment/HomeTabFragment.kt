package com.wgllss.ssmusic.features_ui.page.classics.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel


class HomeTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun isLazyTab() = false

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("推荐", "index", HomeFragment::class.java),
        TabTitleFragment.newInstance("华语", "forum-1", HomeFragment::class.java),
        TabTitleFragment.newInstance("日韩", "forum-15", HomeFragment::class.java),
        TabTitleFragment.newInstance("欧美", "forum-10", HomeFragment::class.java),
        TabTitleFragment.newInstance("remix", "forum-11", HomeFragment::class.java),
        TabTitleFragment.newInstance("纯音乐", "forum-12", HomeFragment::class.java),
        TabTitleFragment.newInstance("异次元", "forum-13", HomeFragment::class.java),
    )
}