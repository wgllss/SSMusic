package com.wgllss.ssmusic.features_ui.page.loadding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wgllss.ssmusic.core.ex.finishActivity
import com.wgllss.ssmusic.core.ex.launchActivity
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_ui.page.home.activity.HomeActivity

class LoadingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchActivity(Intent(this, HomeActivity::class.java))
        finishActivity()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            LogTimer.LogE(this, "onWindowFocusChanged")
        }
    }
}