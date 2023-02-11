package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.switchFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_third.um.UMHelp
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeTabFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SearchFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SettingFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(0) {

    @Inject
    lateinit var homeFragmentL: Lazy<HomeTabFragment>

    @Inject
    lateinit var searchFragmentL: Lazy<SearchFragment>

    @Inject
    lateinit var settingFragmentL: Lazy<SettingFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun initControl(savedInstanceState: Bundle?) {
        LogTimer.LogE(this@HomeActivity, "initControl")
        val contentLayout = LayoutContains.getViewByKey(this, LaunchInflateKey.home_activity)!!
        addContentView(contentLayout, contentLayout.layoutParams)
        setCurrentFragment(homeFragmentL.get())
        LogTimer.LogE(this@HomeActivity, "initControl after")
    }

    override fun initValue() {
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        viewModel.start()
        val navigationView = LayoutContains.getViewByKey(this, LaunchInflateKey.home_navigation)!!
        addContentView(navigationView, navigationView.layoutParams)
        initNavigation(navigationView as BottomNavigationView)
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
                get(1).setIcon(R.drawable.ic_dashboard_black_24dp)
                get(2).setIcon(R.drawable.ic_notifications_black_24dp)
            }
            setOnItemSelectedListener { menu ->
                when (menu.itemId) {
                    R.id.fmt_a -> setCurrentFragment(homeFragmentL.get())
                    R.id.fmt_b -> setCurrentFragment(searchFragmentL.get())
                    R.id.fmt_c -> setCurrentFragment(settingFragmentL.get())
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        switchFragment(fragment, viewModel.mCurrentFragmentTAG, R.id.nav_host_fragment_activity_main)
        viewModel.mCurrentFragmentTAG.delete(0, viewModel.mCurrentFragmentTAG.toString().length)
        viewModel.mCurrentFragmentTAG.append(fragment.javaClass.simpleName)
    }
}