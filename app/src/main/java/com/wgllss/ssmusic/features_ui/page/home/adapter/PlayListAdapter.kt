package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scclzkj.base_core.base.BaseDataBindingAdapter
import com.scclzkj.base_core.extension.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.AdapterMusicPlaylistItemBinding
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import javax.inject.Inject

class PlayListAdapter @Inject constructor() : BaseDataBindingAdapter<MusicTabeBean, AdapterMusicPlaylistItemBinding>() {

    override fun onBindItem(binding: AdapterMusicPlaylistItemBinding, item: MusicTabeBean, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
            musicIcon.loadUrl(item.pic)
            musicVisualizerView.setColor(Color.RED)
            author.setTextColor(if (selectPositon == position) Color.RED else Color.BLACK)
            title.setTextColor(if (selectPositon == position) Color.RED else Color.BLACK)
            musicVisualizerView.visibility = if (selectPositon == position) View.VISIBLE else View.GONE
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_music_playlist_item
}