package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.databinding.FragmentHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.adapter.MusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeTabViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.SettingViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
//@FragmentDestination(pageUrl = "fmt_home", asStarter = true, label = "首页", iconId = R.drawable.ic_home_black_24dp)
class HomeFragment(val title: String, val html: String) : BaseMVVMFragment<HomeViewModel, FragmentHomeBinding>(R.layout.fragment_home) {

    val settingViewModelL = viewModels<HomeTabViewModel>()

    @Inject
    lateinit var musicAdapterL: Lazy<MusicAdapter>

    override fun activitySameViewModel() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = AsyncInflateManager.instance.getInflatedView(inflater.context, R.layout.fragment_home, container, LaunchInflateKey.home_fragment, inflater)
        binding = DataBindingUtil.bind(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogTimer.LogE(this, "$title onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogTimer.LogE(this, "$title onActivityCreated")
        binding?.apply {
            model = this@HomeFragment.viewModel
            adapter = musicAdapterL.get()
            lifecycleOwner = this@HomeFragment
            executePendingBindings()
            rvPlList?.run {
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.getDetailFromSearch(position)
                    }
                })
            }
        }
        settingViewModelL.value.result.observe(viewLifecycleOwner) {
            musicAdapterL.get().notifyData(it)
        }
        settingViewModelL.value.getData(html)
    }

    override fun onResume() {
        super.onResume()
//        viewModel.currentMediaID.observe(viewLifecycleOwner) {
//            playListAdapterL.get().currentMediaID = it
//            playListAdapterL.get().notifyDataSetChanged()
//        }
    }
}