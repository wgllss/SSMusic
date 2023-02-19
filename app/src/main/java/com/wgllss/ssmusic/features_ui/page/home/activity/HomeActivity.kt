package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import com.wgllss.core.activity.PluginActivity
import com.wgllss.core.units.LogTimer
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.plugin.library.HomePluginManagerDispatch
import com.wgllss.plugin.library.HomePluginManagerUser
import com.wgllss.plugin.library.LoadClassSucceedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : PluginActivity() {

    private lateinit var homePluginManagerDispatch: HomePluginManagerDispatch
//    private val homePluginManagerDispatch by lazy { HomePluginManagerUser.getPluginManager() }

//    private lateinit var homeFragment: Fragment
//
//    @Inject
//    lateinit var historyFragmentL: Lazy<HistoryFragment>
//
//    @Inject
//    lateinit var searchFragmentL: Lazy<SearchFragment>
//
//    @Inject
//    lateinit var settingFragmentL: Lazy<SettingFragment>


    override fun getViewModelClass(): Class<out BaseViewModel> {
        LogTimer.LogE(this, "getViewModelClass")

        return homePluginManagerDispatch.getViewModelClass() as Class<out BaseViewModel>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        HomePluginManagerUser.getPluginManager(object : LoadClassSucceedListener {
            override fun onLoadClassSucceed(homeDispatch: HomePluginManagerDispatch) {
                homePluginManagerDispatch = homeDispatch
                homePluginManagerDispatch.attachContext(this@HomeActivity)
                homePluginManagerDispatch.attachViewModel(viewModel)
            }
        })
        super.onCreate(savedInstanceState)
        homePluginManagerDispatch.onCreate(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        homePluginManagerDispatch.onRestoreInstanceState(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        homePluginManagerDispatch.onStart()
    }

    override fun onRestart() {
        super.onRestart()
        homePluginManagerDispatch.onReStart()
    }

    override fun onResume() {
        super.onResume()
        homePluginManagerDispatch.onResume()
    }

    override fun onPause() {
        super.onPause()
        homePluginManagerDispatch.onPause()
    }

    override fun onStop() {
        super.onStop()
        homePluginManagerDispatch.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        homePluginManagerDispatch.onDestory()
    }


    override fun initControl(savedInstanceState: Bundle?) {
        homePluginManagerDispatch.initControl(savedInstanceState)
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        homePluginManagerDispatch.lazyInitValue()
    }
}