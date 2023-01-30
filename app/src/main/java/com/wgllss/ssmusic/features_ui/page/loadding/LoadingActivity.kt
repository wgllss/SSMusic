package com.wgllss.ssmusic.features_ui.page.loadding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.units.LogTimer
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_ui.page.home.activity.HomeActivity
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoadingActivity : ComponentActivity() {

    @Inject
    lateinit var mSSDataBaseL: Lazy<SSDataBase>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogTimer.LogE(this, "onCreate")
        mSSDataBaseL.get()
        launchActivity(Intent(this, HomeActivity::class.java))
    }
}