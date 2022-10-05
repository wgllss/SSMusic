package com.wgllss.ssmusic.features_ui.page.home.activity

import android.view.Menu
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.units.AppConfig
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.widget.navigation.Destination
import com.wgllss.ssmusic.core.widget.navigation.NavGraphBuilder
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(R.layout.activity_home) {


    override fun initValue() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fmt_main)
        var navController = NavHostFragment.findNavController(fragment!!)
        NavGraphBuilder.build(this, navController, fragment.id)
        onPrepareOptionsMenu(binding.bottomNavigationView.getMenu())
        binding.bottomNavigationView.setOnItemSelectedListener {
            navController.navigate(it.itemId)
            return@setOnItemSelectedListener true
        }
        LogTimer.LogE(this, "initValue")
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        lifecycleScope.launch(Dispatchers.IO) {
            menu.clear()
            val destConfig: HashMap<String, Destination> = AppConfig.getDestConfig()
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