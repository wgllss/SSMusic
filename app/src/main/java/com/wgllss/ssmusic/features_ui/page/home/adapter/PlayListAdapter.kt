package com.wgllss.ssmusic.features_ui.page.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.scclzkj.base_core.base.BaseDataBindingAdapter
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.AdapterMusicPlaylistItemBinding
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import javax.inject.Inject

class PlayListAdapter @Inject constructor() : BaseDataBindingAdapter<MusicTabeBean, AdapterMusicPlaylistItemBinding>() {

    override fun onBindItem(binding: AdapterMusicPlaylistItemBinding, item: MusicTabeBean, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_music_playlist_item
}