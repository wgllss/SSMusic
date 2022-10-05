package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.units.AppConfig
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.widget.navigation.Destination
import com.wgllss.ssmusic.core.widget.navigation.NavGraphBuilder
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(R.layout.activity_home) {

    @Inject
    lateinit var AppViewModelL: Lazy<AppViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun initValue() {
        LogTimer.LogE(this, "initValue 0")
        val fragment = supportFragmentManager.findFragmentById(R.id.fmt_main)
        LogTimer.LogE(this, "initValue 1")

        AppViewModelL.get().installHomeJson.observe(this) {
            it?.takeIf { it }?.let { _ ->
                var navController = NavHostFragment.findNavController(fragment!!)
                NavGraphBuilder.build(this, navController, fragment.id)
                onPrepareOptionsMenu(binding.bottomNavigationView.menu)
                binding.bottomNavigationView.setOnItemSelectedListener {
                    navController.navigate(it.itemId)
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        lifecycleScope.launch {
            menu.clear()
            val destConfig: HashMap<String, Destination> = AppConfig.getDestConfig(this@HomeActivity)
            val iterator: Iterator<String> = destConfig.keys.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val destination: Destination? = destConfig[next]
                menu.add(0, destination!!.id, 0, destination.label).setIcon(destination.iconId)
            }
        }
        return true
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            LogTimer.LogE(this, "onWindowFocusChanged")
        }
    }
}