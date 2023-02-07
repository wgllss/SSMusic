package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.fragment.BaseViewModelFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.features_ui.page.home.adapter.MusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeTabViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment(val title: String, private val html: String) : BaseViewModelFragment<HomeViewModel>(0) {

    private val settingViewModelL = viewModels<HomeTabViewModel>()
    private lateinit var rvPlList: RecyclerView

    @Inject
    lateinit var musicAdapterL: Lazy<MusicAdapter>

    override fun activitySameViewModel() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "$title onCreateView")
        val view = LayoutContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!!
        rvPlList = view.findViewById(inflater.context.resources.getIdentifier("rv_pl_list", "id", inflater.context.packageName))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "$title onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogTimer.LogE(this, "$title onActivityCreated")
        rvPlList.adapter = musicAdapterL.get()
        rvPlList?.run {
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    viewModel.getDetailFromSearch(position)
                }
            })
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