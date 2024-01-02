package com.wgllss.ssmusic.features_ui.home.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class KHomeTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("推荐", "", KHomeFragment::class.java),
        TabTitleFragment.newInstance("歌单", "", KSongSheetFragment::class.java),
        TabTitleFragment.newInstance("榜单", "", RankFragment::class.java),
        TabTitleFragment.newInstance("频道", "", KPinDaoFragment::class.java),

    )
}