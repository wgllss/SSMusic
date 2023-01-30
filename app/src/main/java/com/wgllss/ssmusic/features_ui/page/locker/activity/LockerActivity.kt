package com.wgllss.ssmusic.features_ui.page.locker.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.wgllss.ssmusic.R
import com.wgllss.core.activity.BaseMVVMActivity
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.logE
import com.wgllss.core.ex.setFramgment
import com.wgllss.core.widget.DrawerBack
import com.wgllss.ssmusic.databinding.ActivityLockerBinding
import com.wgllss.ssmusic.features_ui.page.locker.fragment.LockerFragment
import com.wgllss.ssmusic.features_ui.page.playing.viewmodels.PlayModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockerActivity : BaseMVVMActivity<PlayModel, ActivityLockerBinding>(R.layout.activity_locker) {
    @Inject
    lateinit var lockerFragmentL: Lazy<LockerFragment>

    override fun beforeSuperOnCreate(savedInstanceState: Bundle?) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            //适配刘海屏
//            val layoutParams = window.attributes
//            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//            window.attributes = layoutParams
//        }
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logE("onNewIntent ")
    }

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        DrawerBack(this).setOnOpenDrawerCompleteListener(object : DrawerBack.OnOpenDrawerCompleteListener {
            override fun onOpenDrawerComplete() = finishActivity()

            override fun onMoveRight(): Boolean = false

        })
        setFramgment(lockerFragmentL.get(), R.id.content)
    }

}