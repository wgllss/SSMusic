package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import android.view.Menu
import androidx.navigation.fragment.NavHostFragment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.units.AppConfig
import com.wgllss.ssmusic.core.widget.navigation.NavGraphBuilder
import com.wgllss.ssmusic.core.widget.navigation.Destination
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(R.layout.activity_home) {


    override fun initValue() {
//        var navController = findNavController(this, R.id.fmt_main)
        val fragment = supportFragmentManager.findFragmentById(R.id.fmt_main)
        var navController = NavHostFragment.findNavController(fragment!!)
        NavGraphBuilder.build(this, navController, fragment.id)
//        setupWithNavController(binding.bottomNavigationView, navController)
        onPrepareOptionsMenu(binding.bottomNavigationView.getMenu())
        binding.bottomNavigationView.setOnItemSelectedListener {
            navController.navigate(it.getItemId())
//            when (it.itemId) {
//                R.id.fmt_a -> {
//                    navController.navigate(R.id.action_fmta, Bundle())
//                }
//                R.id.fmt_b -> {
//                    navController.navigate(R.id.action_fmtb, Bundle())
//                }
//                R.id.fmt_c -> {
//                    navController.navigate(R.id.action_fmtc, Bundle())
//                }
//            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        val destConfig: HashMap<String, Destination> = AppConfig.getDestConfig()
        val iterator: Iterator<String> = destConfig.keys.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val destination: Destination? = destConfig[next]
            menu.add(0, destination!!.id, 0, destination.label).setIcon(destination.iconId)
        }
        return true
    }

    override fun onBackPressed() {
        exitApp()
    }
}