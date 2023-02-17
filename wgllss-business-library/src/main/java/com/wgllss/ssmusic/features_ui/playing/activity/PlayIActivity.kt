package com.wgllss.ssmusic.features_ui.playing.activity
//
//import android.os.Bundle
//import com.wgllss.core.activity.BaseMVVMActivity
//import com.wgllss.core.ex.setFramgment
//import com.wgllss.ssmusic.features_ui.playing.fragment.PlayFragment
//import com.wgllss.ssmusic.features_ui.playing.viewmodels.PlayModel
//import com.wgllss.ssmusic.library.databinding.ActivityPlayBinding
//import dagger.Lazy
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//import com.wgllss.ssmusic.library.R
//
//@AndroidEntryPoint
//class PlayActivity : BaseMVVMActivity<PlayModel, ActivityPlayBinding>(R.layout.activity_play) {
//
//    @Inject
//    lateinit var playFragmentL: Lazy<PlayFragment>
//
//    override fun initControl(savedInstanceState: Bundle?) {
//        super.initControl(savedInstanceState)
//        setFramgment(playFragmentL.get(), R.id.content)
//    }
//}