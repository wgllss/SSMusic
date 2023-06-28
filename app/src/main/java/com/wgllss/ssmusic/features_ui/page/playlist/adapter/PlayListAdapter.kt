package com.wgllss.ssmusic.features_ui.page.playlist.adapter

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.core.adapter.BaseDataBindingAdapter
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.AdapterMusicPlaylistItemBinding
import javax.inject.Inject

class PlayListAdapter @Inject constructor() : BaseDataBindingAdapter<MediaBrowserCompat.MediaItem, AdapterMusicPlaylistItemBinding>() {

    var currentMediaID: String = ""

    private var colorInt: Int = 0
    private var colorPrimary: Int = 0

    private fun getAndroidColorBackground(context: Context): Int {
        if (colorInt == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            colorInt = typedValue.data
        }
        return colorInt
    }

    private fun getColorPrimary(context: Context): Int {
        if (colorPrimary == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
            colorPrimary = typedValue.data
        }
        return colorPrimary
    }

    private var blockDelete: ((id: Long) -> Unit)? = null

    fun setBlockDelete(blockDelete: ((id: Long) -> Unit)? = null) {
        this.blockDelete = blockDelete
    }


    override fun onBindItem(binding: AdapterMusicPlaylistItemBinding, item: MediaBrowserCompat.MediaItem, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
            musicIcon.loadUrl(item.description.iconUri.toString())
            musicVisualizerView.setColor(getColorPrimary(context!!))
            author.setTextColor(if (currentMediaID == item.mediaId) getColorPrimary(context!!) else getAndroidColorBackground(context!!))
            title.setTextColor(if (currentMediaID == item.mediaId) getColorPrimary(context!!) else getAndroidColorBackground(context!!))
            musicVisualizerView.visibility = if (currentMediaID == item.mediaId) View.VISIBLE else View.GONE
            deleteRightTv.setOnClickListener {
                blockDelete?.invoke(item.mediaId?.toLong() ?: 0L)
            }
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_music_playlist_item
}