package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.graphics.Color
import android.support.v4.media.MediaBrowserCompat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.core.adapter.BaseDataBindingAdapter
import com.wgllss.ssmusic.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.AdapterMusicPlaylistItemBinding
import javax.inject.Inject

class PlayListAdapter @Inject constructor() : BaseDataBindingAdapter<MediaBrowserCompat.MediaItem, AdapterMusicPlaylistItemBinding>() {

    override fun onBindItem(binding: AdapterMusicPlaylistItemBinding, item: MediaBrowserCompat.MediaItem, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
            musicIcon.loadUrl(item.description.iconUri.toString())
            musicVisualizerView.setColor(Color.RED)
            author.setTextColor(if (selectPositon == position) Color.RED else Color.BLACK)
            title.setTextColor(if (selectPositon == position) Color.RED else Color.BLACK)
            musicVisualizerView.visibility = if (selectPositon == position) View.VISIBLE else View.GONE
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_music_playlist_item
}