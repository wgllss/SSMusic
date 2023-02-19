package com.wgllss.dynamic.sample

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.core.activity.BaseViewModelClassActivity
import com.wgllss.core.ex.switchFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.music.skin.R
import com.wgllss.plugin.library.HomePluginManagerImpl
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.InitHomeFirstInitializeHelp
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_third.um.UMHelp
import com.wgllss.ssmusic.features_ui.home.fragment.HomeTabFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SampleHomePluginManagerImpl : HomePluginManagerImpl {

    private lateinit var viewModel: HomeViewModel //by (activity as BaseViewModelClassActivity).lazyViewModels() as Lazy<HomeViewModel>

    private lateinit var homeFragment: Fragment
    private lateinit var activity: FragmentActivity
    private lateinit var res: Resources

    override fun attachContext(context: FragmentActivity) {
        activity = context
        res = context.resources

    }

    override fun getViewModelClass(): Class<out Any> {
        return HomeViewModel::class.java
    }

    override fun attachViewModel(mViewModel: ViewModel) {
        viewModel = mViewModel as HomeViewModel
    }

    override fun initControl(savedInstanceState: Bundle?) {
        LogTimer.LogE(this@SampleHomePluginManagerImpl, "initControl savedInstanceState $savedInstanceState")
        val contentLayout = HomeContains.getViewByKey(activity, LaunchInflateKey.home_activity)!!
        activity?.addContentView(contentLayout, contentLayout.layoutParams)
        if (savedInstanceState == null) {
            homeFragment = HomeContains.getFragmentByKey(LaunchInflateKey.home_tab_fragment) ?: HomeTabFragment()
            setCurrentFragment(homeFragment)
        }
        LogTimer.LogE(this@SampleHomePluginManagerImpl, "initControl after")
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        viewModel.lazyTabView()
        val navigationView = HomeContains.getViewByKey(activity, LaunchInflateKey.home_navigation)!! as BottomNavigationView
        activity?.addContentView(navigationView, navigationView.layoutParams)
        initNavigation(navigationView)
        viewModel.start()
        viewModel.rootMediaId.observe(activity) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            UMHelp.umInit(activity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
    }

    override fun onDestory() {
    }

    override fun onPause() {
    }

    override fun onReStart() {
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    }

    override fun onResume() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun preIntHome(context: Context) {
        InitHomeFirstInitializeHelp.initCreate(context)
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
//        SearchFragment::class.java.simpleName -> 1
//        SettingFragment::class.java.simpleName -> 2
        else -> 0
    }

    private fun onNavBarItemSelected(itemId: Int): Boolean {
//        when (itemId) {
////            R.id.fmt_a -> setCurrentFragment(homeFragmentL.get())
//            R.id.fmt_a -> {
//                if (!this::homeFragment.isInitialized || homeFragment == null) {
//                    homeFragment = HomeContains.getFragmentByKey(LaunchInflateKey.home_tab_fragment) ?: HomeTabFragment()
//                }
//                setCurrentFragment(homeFragment)
//            }
////            R.id.fmt_b -> setCurrentFragment(historyFragmentL.get())
////            R.id.fmt_c -> setCurrentFragment(searchFragmentL.get())
////            R.id.fmt_d -> setCurrentFragment(settingFragmentL.get())
//        }
        return true
    }

    private fun setCurrentFragment(fragment: Fragment) {
        LogTimer.LogE(this, "setCurrentFragment fragment:${fragment.javaClass.simpleName}")
        activity.switchFragment(fragment, viewModel.mCurrentFragmentTAG, R.id.nav_host_fragment_activity_main)
        viewModel.mCurrentFragmentTAG.delete(0, viewModel.mCurrentFragmentTAG.toString().length)
        viewModel.mCurrentFragmentTAG.append(fragment.javaClass.simpleName)
    }


}