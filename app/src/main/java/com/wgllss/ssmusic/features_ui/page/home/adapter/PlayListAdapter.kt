package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.content.Context
import android.graphics.Color
import android.support.v4.media.MediaBrowserCompat
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.core.adapter.BaseDataBindingAdapter
import com.wgllss.ssmusic.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.AdapterMusicPlaylistItemBinding
import javax.inject.Inject

class PlayListAdapter @Inject constructor() : BaseDataBindingAdapter<MediaBrowserCompat.MediaItem, AdapterMusicPlaylistItemBinding>() {

    var currentMediaID: String = ""

    var colorInt: Int = 0

    private fun getAndroidColorBackground(context: Context): Int {
        if (colorInt == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            colorInt = typedValue.data
        }
        return colorInt
    }

    private var blockDelete: ((id: Long) -> Unit)? = null

    fun setBlockDelete(blockDelete: ((id: Long) -> Unit)? = null) {
        this.blockDelete = blockDelete
    }


    override fun onBindItem(binding: AdapterMusicPlaylistItemBinding, item: MediaBrowserCompat.MediaItem, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
            musicIcon.loadUrl(item.description.iconUri.toString())
            musicVisualizerView.setColor(Color.RED)
            author.setTextColor(if (currentMediaID == item.mediaId) Color.RED else getAndroidColorBackground(context!!))
            title.setTextColor(if (currentMediaID == item.mediaId) Color.RED else getAndroidColorBackground(context!!))
            musicVisualizerView.visibility = if (currentMediaID == item.mediaId) View.VISIBLE else View.GONE
            deleteRightTv.setOnClickListener {
                blockDelete?.invoke(item.mediaId?.toLong() ?: 0L)
            }
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_music_playlist_item
}