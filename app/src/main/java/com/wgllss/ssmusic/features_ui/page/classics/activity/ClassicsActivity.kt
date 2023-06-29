package com.wgllss.ssmusic.features_ui.page.classics.activity
//
//import android.os.Bundle
//import com.wgllss.core.activity.BaseMVVMActivity
//import com.wgllss.core.ex.setFramgment
//import com.wgllss.ssmusic.R
//import com.wgllss.ssmusic.databinding.ActivityPlayBinding
//import com.wgllss.ssmusic.features_ui.page.classics.fragment.HomeTabFragment
//import com.wgllss.ssmusic.features_ui.page.playlist.fragment.HistoryFragment
//import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2
//import com.wgllss.ssmusic.features_ui.page.search.fragment.SearchFragment
//import dagger.Lazy
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
////@AndroidEntryPoint
//class ClassicsActivity : BaseMVVMActivity<HomeViewModel2, ActivityPlayBinding>(R.layout.activity_play) {
//    val homeTabFragment by lazy { HomeTabFragment() }
//
//    override fun initControl(savedInstanceState: Bundle?) {
//        super.initControl(savedInstanceState)
//        setFramgment(homeTabFragment, R.id.content)
//    }
//
//    override fun initValue() {
//        super.initValue()
//        viewModel.lazyTabView()
//    }
//}