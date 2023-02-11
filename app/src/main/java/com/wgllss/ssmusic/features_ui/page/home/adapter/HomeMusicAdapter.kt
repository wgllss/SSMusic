package com.wgllss.ssmusic.features_ui.page.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.BaseRecyclerAdapter
import com.wgllss.ssmusic.data.MusicItemBean

class HomeMusicAdapter : BaseRecyclerAdapter<MusicItemBean>() {

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_home_item_music

    override fun onBindItem(item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<MaterialButton>(R.id.author).text = item.author
        holder.itemView.findViewById<MaterialTextView>(R.id.mater_music_name).text = item.musicName
        holder.itemView.findViewById<MaterialTextView>(R.id.sample_hz).text = item.samplingRate
    }
}