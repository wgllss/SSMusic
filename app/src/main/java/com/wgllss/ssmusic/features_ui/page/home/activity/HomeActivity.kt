package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.setFramgment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(R.layout.activity_home) {

    @Inject
    lateinit var homeFragmentL: Lazy<HomeFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun initControl(savedInstanceState: Bundle?) {
        LogTimer.LogE(this@HomeActivity, "initControl")
        val contentLayout = LayoutContains.getViewByKey(this, LaunchInflateKey.home_activity)!!
        addContentView(contentLayout, contentLayout.layoutParams)
        setFramgment(homeFragmentL.get(), R.id.nav_host_fragment_activity_main)
        LogTimer.LogE(this@HomeActivity, "initControl after")

//        AsyncInflateManager.instance.getAsynInflatedView(this, LaunchInflateKey.home_activity, object : OnInflateFinishListener {
//            override fun onInflateFinished(view: View) {
//                setContentView(view)
//                binding = DataBindingUtil.bind(view)!!
//                setFramgment(homeFragmentL.get(), R.id.nav_host_fragment_activity_main)
//                LogTimer.LogE(this@HomeActivity, "super initControl after")
//        AsyncInflateManager.instance.getAsynInflatedView(this@HomeActivity, LaunchInflateKey.home_navigation, object : OnInflateFinishListener {
//            override fun onInflateFinished(view: View) {
//                LogTimer.LogE(this@HomeActivity, "onInflateFinished")
//                lifecycleScope.launch {
//                    val lpA = async(Dispatchers.IO) {
//                        FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.navigation_height).toInt()).apply {
//                            gravity = Gravity.BOTTOM
//                        }
//                    }
//                    view.parent?.takeIf {
//                        it is ViewGroup
//                    }?.let {
//                        (it as ViewGroup).removeView(view)
//                    }
//                    addContentView(view, lpA.await())
//                }
//            }
//        })
//            }
//        })
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
}