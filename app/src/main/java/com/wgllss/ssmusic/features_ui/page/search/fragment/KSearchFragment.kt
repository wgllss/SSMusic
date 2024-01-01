package com.wgllss.ssmusic.features_ui.page.search.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import com.wgllss.core.ex.HideSoftInputFromWindow
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentKsearchBinding
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import com.wgllss.ssmusic.features_ui.page.search.adapter.KSearchAdapter
import com.wgllss.ssmusic.features_ui.page.search.viewmodels.HomeViewModel3
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class KSearchFragment : TabTitleFragment<HomeViewModel3>() {

    private lateinit var binding: FragmentKsearchBinding

    private val kSearchAdapterL by lazy { KSearchAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::binding.isInitialized)
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ksearch, container, false)
        return binding.root
    }

//    override fun activitySameViewModel() = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            model = this@KSearchFragment.viewModel
            adapter = kSearchAdapterL
            lifecycleOwner = this@KSearchFragment
            executePendingBindings()
            rvResult?.run {
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.doPlay(kSearchAdapterL.getItem(position))
                    }
                })
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
                kSearchAdapterL.notifyData(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.nowPlay.postValue(false)
    }

    fun searchKey(content: String) {
        viewModel.searchKeyByTitle(content)
    }
}