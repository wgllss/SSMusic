package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.core.activity.BaseViewModelActivity
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.switchFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_third.um.UMHelp
import com.wgllss.ssmusic.features_ui.page.home.fragment.HistoryFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SearchFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SettingFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseViewModelActivity<HomeViewModel>() {

    //    @Inject
//    lateinit var homeFragmentL: Lazy<HomeTabFragment>
    lateinit var homeFragment: Fragment

    @Inject
    lateinit var historyFragmentL: Lazy<HistoryFragment>

    @Inject
    lateinit var searchFragmentL: Lazy<SearchFragment>

    @Inject
    lateinit var settingFragmentL: Lazy<SettingFragment>

//    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        LogTimer.LogE(this, "onRestoreInstanceState ${viewModel.mCurrentFragmentTAG.toString()}")
    }

    override fun initControl(savedInstanceState: Bundle?) {
        LogTimer.LogE(this@HomeActivity, "initControl savedInstanceState $savedInstanceState")
        val contentLayout = LayoutContains.getViewByKey(this, LaunchInflateKey.home_activity)!!
        addContentView(contentLayout, contentLayout.layoutParams)
        if (savedInstanceState == null) {
//            setCurrentFragment(homeFragmentL.get())
            homeFragment = LayoutContains.getFragmentByKey(LaunchInflateKey.home_tab_fragment)
            setCurrentFragment(homeFragment)
        }
        LogTimer.LogE(this@HomeActivity, "initControl after")
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        viewModel.lazyTabView()
        val navigationView = LayoutContains.getViewByKey(this, LaunchInflateKey.home_navigation)!! as BottomNavigationView
        addContentView(navigationView, navigationView.layoutParams)
        initNavigation(navigationView)
        viewModel.start()
        viewModel.rootMediaId.observe(this) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            UMHelp.umInit(this@HomeActivity)
        }
    }

    private fun initNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.apply {
            with(menu) {
                get(0).setIcon(R.drawable.ic_home_black_24dp)
                get(1).setIcon(R.drawable.ic_round_queue_music_24)
                get(2).setIcon(R.drawable.ic_dashboard_black_24dp)
                get(3).setIcon(R.drawable.ic_notifications_black_24dp)
            }
            if (viewModel.isFirst) viewModel.isFirst = false else selectedItemId = menu.getItem(getItemId()).itemId
            setOnItemSelectedListener {
                return@setOnItemSelectedListener onNavBarItemSelected(it.itemId)
            }
        }

    }

    private fun getItemId() = when (viewModel.mCurrentFragmentTAG.toString()) {
        "HomeTabFragment" -> 0
        SearchFragment::class.java.simpleName -> 1
        SettingFragment::class.java.simpleName -> 2
        else -> 0
    }

    private fun onNavBarItemSelected(itemId: Int): Boolean {
        when (itemId) {
//            R.id.fmt_a -> setCurrentFragment(homeFragmentL.get())
            R.id.fmt_a -> {
                if (!this::homeFragment.isInitialized || homeFragment == null) {
                    homeFragment = LayoutContains.getFragmentByKey(LaunchInflateKey.home_tab_fragment)
                }
                setCurrentFragment(homeFragment)
            }
            R.id.fmt_b -> setCurrentFragment(historyFragmentL.get())
            R.id.fmt_c -> setCurrentFragment(searchFragmentL.get())
            R.id.fmt_d -> setCurrentFragment(settingFragmentL.get())
        }
        return true
    }

    private fun setCurrentFragment(fragment: Fragment) {
        LogTimer.LogE(this, "setCurrentFragment fragment:${fragment.javaClass.simpleName}")
        switchFragment(fragment, viewModel.mCurrentFragmentTAG, R.id.nav_host_fragment_activity_main)
        viewModel.mCurrentFragmentTAG.delete(0, viewModel.mCurrentFragmentTAG.toString().length)
        viewModel.mCurrentFragmentTAG.append(fragment.javaClass.simpleName)
    }
}