package com.wgllss.ssmusic.features_ui.page.home.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class KHomeMVTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("新歌", "9", KMVListFragment::class.java),
        TabTitleFragment.newInstance("华语", "13", KMVListFragment::class.java),
        TabTitleFragment.newInstance("日韩", "17", KMVListFragment::class.java),
        TabTitleFragment.newInstance("欧美", "16", KMVListFragment::class.java)
    )
}