package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.scclzkj.base_core.extension.HideSoftInputFromWindow
import com.scclzkj.base_core.widget.OnRecyclerViewItemClickListener
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentSearchBinding
import com.wgllss.ssmusic.features_ui.page.home.adapter.MusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@FragmentDestination(pageUrl = "fmt_search", label = "搜索", iconId = R.drawable.ic_dashboard_black_24dp)
@AndroidEntryPoint
class SearchFragment : BaseMVVMFragment<HomeViewModel, FragmentSearchBinding>(R.layout.fragment_search) {

    @Inject
    lateinit var musicAdapterL: Lazy<MusicAdapter>

    override fun activitySameViewModel() = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            model = this@SearchFragment.viewModel
            adapter = musicAdapterL.get()
            lifecycleOwner = this@SearchFragment
            executePendingBindings()
            shapeableSearch.setOnClickListener {
                viewModel.searchKeyByTitle()
                HideSoftInputFromWindow(binding.root)
            }
            rvResult?.run {
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.getDetailFromSearch(position)
                    }
                })
            }
            binding.etName.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        viewModel.searchKeyByTitle()
                        HideSoftInputFromWindow(binding.root)
                    }
                    else -> {}
                }
                true
            }
        }
        viewModel.result.observe(viewLifecycleOwner) {
            musicAdapterL.get().notifyData(it)
        }
    }
}