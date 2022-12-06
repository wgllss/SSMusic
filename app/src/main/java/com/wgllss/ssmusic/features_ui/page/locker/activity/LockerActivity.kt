package com.wgllss.ssmusic.features_ui.page.locker.activity

import android.os.Bundle
import android.view.WindowManager
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.ex.finishActivity
import com.wgllss.ssmusic.core.ex.setFramgment
import com.wgllss.ssmusic.core.widget.DrawerBack
import com.wgllss.ssmusic.databinding.ActivityLockerBinding
import com.wgllss.ssmusic.features_ui.page.locker.fragment.LockerFragment
import com.wgllss.ssmusic.features_ui.page.playing.viewmodels.PlayModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockerActivity : BaseMVVMActivity<PlayModel, ActivityLockerBinding>(R.layout.activity_locker) {

    override fun berforeSuperOnCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    }

    @Inject
    lateinit var lockerFragmentL: Lazy<LockerFragment>

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        DrawerBack(this).setOnOpenDrawerCompleteListener(object : DrawerBack.OnOpenDrawerCompleteListener {
            override fun onOpenDrawerComplete() = finishActivity()

            override fun onMoveRight(): Boolean = false

        })
        setFramgment(lockerFragmentL.get(), R.id.content)
    }

}