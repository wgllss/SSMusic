package com.wgllss.ssmusic.core.units

import android.content.Context
import android.content.Intent
import com.wgllss.ssmusic.features_system.music.MusicConstants
import com.wgllss.ssmusic.features_ui.page.home.activity.HomeActivity

object NavigationUtils {

    fun getNowPlayingIntent(context: Context): Intent {
        val intent = Intent(context, HomeActivity::class.java)
        intent.action = MusicConstants.NAVIGATE_NOWPLAYING
        return intent
    }
}