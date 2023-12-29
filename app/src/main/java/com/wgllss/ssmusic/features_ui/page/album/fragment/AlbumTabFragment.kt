package com.wgllss.ssmusic.features_ui.page.album.fragment

import androidx.fragment.app.Fragment
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class AlbumTabFragment : BaseTabFragment<HomeViewModel>() {

    override fun isLazyTab() = false

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("华语", "1-1-1.html", KAlbumFragment::class.java),
        TabTitleFragment.newInstance("日本", "1-1-2.html", KAlbumFragment::class.java),
        TabTitleFragment.newInstance("欧美", "1-1-3.html", KAlbumFragment::class.java),
        TabTitleFragment.newInstance("韩语", "1-1-4.html", KAlbumFragment::class.java),
    )

}