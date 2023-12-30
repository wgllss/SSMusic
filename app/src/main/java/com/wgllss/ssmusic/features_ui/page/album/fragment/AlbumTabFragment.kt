package com.wgllss.ssmusic.features_ui.page.album.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class AlbumTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun isLazyTab() = false

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("华语", "1", KAlbumFragment::class.java),
        TabTitleFragment.newInstance("欧美", "2", KAlbumFragment::class.java),
        TabTitleFragment.newInstance("日本", "3", KAlbumFragment::class.java),
        TabTitleFragment.newInstance("韩语", "4", KAlbumFragment::class.java),
    )
}