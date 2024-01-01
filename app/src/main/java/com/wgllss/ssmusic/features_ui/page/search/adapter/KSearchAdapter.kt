package com.wgllss.ssmusic.features_ui.page.search.adapter

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.data.MusicItemBean
import javax.inject.Inject

//class KSearchAdapter @Inject constructor() : BaseRecyclerAdapter<MusicItemBean>() {
class KSearchAdapter : BaseRecyclerAdapter<MusicItemBean>() {
    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_search_k

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder?.itemView?.run {
            findViewById<TextView>(R.id.author).text = item.author
            findViewById<TextView>(R.id.title).text = item.musicName
            findViewById<ShapeableImageView>(R.id.img_song).run {
                loadUrl(item.album_sizable_cover)
                shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                    setAllCorners(RoundedCornerTreatment())
                    setAllCornerSizes(context.getIntToDip(40f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                }.build()
            }
        }
    }

}