package com.wgllss.ssmusic.features_ui.page.home.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class KHomeSingerTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("华语", "1", KSingersFragment::class.java),
        TabTitleFragment.newInstance("韩国", "6", KSingersFragment::class.java),
        TabTitleFragment.newInstance("日本", "5", KSingersFragment::class.java),
        TabTitleFragment.newInstance("欧美", "2", KSingersFragment::class.java),
        TabTitleFragment.newInstance("音乐人", "", KSingersFragment::class.java),
        TabTitleFragment.newInstance("其他", "4", KSingersFragment::class.java)
    )
}