package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.ssmusic.NavigationConfig
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.OnInflateFinishListener
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.widget.navigation.NavGraphBuilder
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(R.layout.activity_home) {
    @Inject
    lateinit var mSSDataBaseL: Lazy<SSDataBase>

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun initControl(savedInstanceState: Bundle?) {
        mSSDataBaseL.get()
        super.initControl(savedInstanceState)
        val fragment = supportFragmentManager.findFragmentById(R.id.fmt_main)
        val navController = NavHostFragment.findNavController(fragment!!)
        viewModel.rootMediaId.observe(this) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
        NavGraphBuilder.build(this@HomeActivity, navController, fragment.id)
        LogTimer.LogE(this@HomeActivity, "super initControl after")
        AsyncInflateManager.instance.getAsynInflatedView(this, LaunchInflateKey.home, object : OnInflateFinishListener {
            override fun onInflateFinished(view: View) {
                LogTimer.LogE(this@HomeActivity, "onInflateFinished")
                lifecycleScope.launch {
                    val lpA = async(Dispatchers.IO) {
                        FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.navigation_height).toInt()).apply {
                            gravity = Gravity.BOTTOM
                        }
                    }
                    val menu = async(Dispatchers.IO) {
                        view?.takeIf {
                            it is BottomNavigationView
                        }?.let {
                            (it as BottomNavigationView)?.apply {
                                setOnItemSelectedListener {
                                    navController.navigate(it.itemId)
                                    return@setOnItemSelectedListener true
                                }
                            }?.run {
                                menu
                            }
                        }
                    }
                    view.parent?.takeIf {
                        it is ViewGroup
                    }?.let {
                        (it as ViewGroup).removeView(view)
                    }
                    binding?.frameMain?.addView(view, lpA.await())
                    onPrepareOptionsMenu(menu!!.await()!!)
                }
            }
        })
    }

    override fun initValue() {
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        NavigationConfig.getDestConfig()?.forEach {
            it?.value?.run {
                menu.add(0, id, 0, label).setIcon(iconId)
            }
        }
        super.onPrepareOptionsMenu(menu)
        return true
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        bindService()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun bindService() {
        logE("bindService")
    }
}