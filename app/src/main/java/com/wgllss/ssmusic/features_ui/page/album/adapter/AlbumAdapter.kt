package com.wgllss.ssmusic.features_ui.page.album.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.ssmusic.data.MusicItemBean

class AlbumAdapter : BaseRecyclerAdapter<MusicItemBean>() {
    override fun getLayoutResId(viewType: Int) = 0

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
    }

}