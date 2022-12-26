package com.wgllss.ssmusic.features_ui.page.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.core.adapter.BaseDataBindingAdapter
import com.wgllss.ssmusic.databinding.AdapterItemMusicBinding
import com.wgllss.music.datasourcelibrary.data.MusicItemBean
import com.wgllss.ssmusic.R
import javax.inject.Inject

class MusicAdapter @Inject constructor() : BaseDataBindingAdapter<MusicItemBean, AdapterItemMusicBinding>() {
    override fun onBindItem(binding: AdapterItemMusicBinding, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_music
}