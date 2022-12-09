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

    var currentMediaID: String = ""

    private var blockDelete: ((id: Long) -> Unit)? = null

    fun setBlockDelete(blockDelete: ((id: Long) -> Unit)? = null) {
        this.blockDelete = blockDelete
    }


    override fun onBindItem(binding: AdapterMusicPlaylistItemBinding, item: MediaBrowserCompat.MediaItem, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
            musicIcon.loadUrl(item.description.iconUri.toString())
            musicVisualizerView.setColor(Color.RED)
            author.setTextColor(if (currentMediaID == item.mediaId) Color.RED else Color.BLACK)
            title.setTextColor(if (currentMediaID == item.mediaId) Color.RED else Color.BLACK)
            musicVisualizerView.visibility = if (currentMediaID == item.mediaId) View.VISIBLE else View.GONE
            deleteRightTv.setOnClickListener {
                blockDelete?.invoke(item.mediaId?.toLong() ?: 0L)
            }
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_music_playlist_item
}