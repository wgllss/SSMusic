package com.wgllss.ssmusic.features_ui.page.search.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.wgllss.core.ex.HideSoftInputFromWindow
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentKsearchBinding
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import com.wgllss.ssmusic.features_ui.page.search.adapter.KSearchAdapter
import com.wgllss.ssmusic.features_ui.page.search.viewmodels.HomeViewModel3
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KSearchFragment @Inject constructor() : BaseMVVMFragment<HomeViewModel3, FragmentKsearchBinding>(R.layout.fragment_ksearch) {

    @Inject
    lateinit var kSearchAdapterL: Lazy<KSearchAdapter>

//    override fun activitySameViewModel() = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            model = this@KSearchFragment.viewModel
            adapter = kSearchAdapterL.get()
            lifecycleOwner = this@KSearchFragment
            executePendingBindings()
            shapeableSearch.setOnClickListener {
                viewModel.searchKeyByTitle()
                HideSoftInputFromWindow(root)
            }
            rvResult?.run {
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.doPlay(kSearchAdapterL.get().getItem(position))
                    }
                })
            }
            etName.requestFocus()
            etName.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        viewModel.searchKeyByTitle()
                        HideSoftInputFromWindow(root)
                        true
                    }
                    else -> {}
                }
                false
            }
            imgBack.setOnClickListener {
                activity?.run {
                    HideSoftInputFromWindow(root)
                    finishActivity()
                }
            }
        }
    }

    override fun initObserve() {
        super.initObserve()
        viewModel?.run {
            nowPlay.observe(viewLifecycleOwner) {
                it?.takeIf {
                    it
                }?.let {
                    activity?.run {
                        launchActivity(Intent(this, PlayActivity::class.java))
                    }
                }
            }
            result.observe(viewLifecycleOwner) {
                kSearchAdapterL.get().notifyData(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.nowPlay.postValue(false)
    }
}