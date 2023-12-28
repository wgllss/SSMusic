package com.wgllss.ssmusic.features_ui.home.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class KHomeTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("推荐", "", KHomeFragment::class.java),
        TabTitleFragment.newInstance("歌单", "", KSongSheetFragment::class.java),
        TabTitleFragment.newInstance("榜单", "", RankFragment::class.java),
        TabTitleFragment.newInstance("频道", "", KPinDaoFragment::class.java),
        TabTitleFragment.newInstance("华语", "https://m.kugou.com/newsong/index", KNewLisFragment::class.java),
        TabTitleFragment.newInstance("欧美", "https://m.kugou.com/newsong/index/2", KNewLisFragment::class.java),
        TabTitleFragment.newInstance("韩国", "https://m.kugou.com/newsong/index/4", KNewLisFragment::class.java),
        TabTitleFragment.newInstance("日本", "https://m.kugou.com/newsong/index/5", KNewLisFragment::class.java)
    )
}