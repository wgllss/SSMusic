package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.core.activity.BaseViewModelActivity
import com.wgllss.core.ex.switchFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_third.um.UMHelp
import com.wgllss.ssmusic.features_ui.home.fragment.HomeTabFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.home.fragment.HistoryFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SearchFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SettingFragment
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseViewModelActivity<HomeViewModel>() {
    private val homeFragment by lazy { HomeContains.getFragmentByKey(LaunchInflateKey.home_tab_fragment) }



    @Inject
    lateinit var historyFragmentL: Lazy<HistoryFragment>

    @Inject
    lateinit var searchFragmentL: Lazy<SearchFragment>

    @Inject
    lateinit var settingFragmentL: Lazy<SettingFragment>

    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }

    override fun initControl(savedInstanceState: Bundle?) {
        LogTimer.LogE(this@HomeActivity, "initControl savedInstanceState $savedInstanceState")
        val contentLayout = HomeContains.getViewByKey(this, LaunchInflateKey.home_activity)!!
        addContentView(contentLayout, contentLayout.layoutParams)
        if (savedInstanceState == null) {
            setCurrentFragment(homeFragment)
        } else {
            setNavigation()
            onNavBarItemSelected(navigationView.menu.getItem(getItemId()).itemId)
        }
        LogTimer.LogE(this@HomeActivity, "initControl after")
        window.setBackgroundDrawable(null)//去掉主题背景颜色
    }

    override fun onBackPressed() {
        exitApp()
    }

    private fun setNavigation() {
        if (!this::navigationView.isInitialized)
            navigationView = HomeContains.getViewByKey(this, LaunchInflateKey.home_navigation)!! as BottomNavigationView
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        if (viewModel.isFirst) {
            viewModel.lazyTabView()
            viewModel.isFirst = false
            setNavigation()
        }
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
        HistoryFragment::class.java.simpleName -> 1
        SearchFragment::class.java.simpleName -> 2
        SettingFragment::class.java.simpleName -> 3
        else -> 0
    }

    private fun onNavBarItemSelected(itemId: Int): Boolean {
        when (itemId) {
            R.id.fmt_a -> {
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