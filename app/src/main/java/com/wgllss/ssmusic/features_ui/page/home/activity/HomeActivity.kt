package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.ssmusic.R
import com.wgllss.core.activity.BaseMVVMActivity
import com.wgllss.core.asyninflater.AsyncInflateManager
import com.wgllss.core.asyninflater.LaunchInflateKey
import com.wgllss.core.asyninflater.LayoutContains
import com.wgllss.core.asyninflater.OnInflateFinishListener
import com.wgllss.core.ex.switchFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SearchFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SettingFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(R.layout.activity_home) {

    @Inject
    lateinit var homeFragmentL: Lazy<HomeFragment>

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
        initNavigation()
    }

    override fun initValue() {
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        viewModel.start()
        viewModel.rootMediaId.observe(this) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
    }

    private fun initNavigation() {
        AsyncInflateManager.instance.getAsynInflatedView(this, LaunchInflateKey.home_navigation, object : OnInflateFinishListener {
            override fun onInflateFinished(view: View) {
                view.takeIf {
                    it.parent != null
                }?.let {
                    (it.parent as ViewGroup).removeView(it)
                }
                addContentView(view, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.navigation_height).toInt()).apply {
                    gravity = Gravity.BOTTOM
                })
                view?.takeIf {
                    it is BottomNavigationView
                }?.let {
                    (it as BottomNavigationView).setOnItemSelectedListener { menu ->
                        when (menu.itemId) {
                            R.id.fmt_a -> setCurrentFragment(homeFragmentL.get())
                            R.id.fmt_b -> setCurrentFragment(searchFragmentL.get())
                            R.id.fmt_c -> setCurrentFragment(settingFragmentL.get())
                        }
                        return@setOnItemSelectedListener true
                    }
                }
            }
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        switchFragment(fragment, viewModel.mCurrentFragmentTAG, R.id.nav_host_fragment_activity_main)
        viewModel.mCurrentFragmentTAG.delete(0, viewModel.mCurrentFragmentTAG.toString().length)
        viewModel.mCurrentFragmentTAG.append(fragment.javaClass.simpleName)
    }
}