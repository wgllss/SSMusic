package com.wgllss.ssmusic.features_ui.home.adapter
//
//import android.content.Context
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.imageview.ShapeableImageView
//import com.google.android.material.shape.RoundedCornerTreatment
//import com.google.android.material.shape.ShapeAppearanceModel
//import com.wgllss.core.adapter.BaseRecyclerAdapter
//import com.wgllss.core.ex.getIntToDip
//import com.wgllss.core.ex.loadUrl
//
//class NewSongAdapter : BaseRecyclerAdapter<KMusicItemBean>() {
//
//    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_new_song
//
//    override fun onBindItem(context: Context, item: KMusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
//        holder?.itemView?.run {
//            findViewById<ShapeableImageView>(R.id.music_icon).apply {
//                loadUrl(item.imgUrl)
//                shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
//                    setAllCorners(RoundedCornerTreatment())
//                    setAllCornerSizes(context.getIntToDip(40f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
//                }.build()
//            }
//            findViewById<TextView>(R.id.author).text = item.author
//            findViewById<TextView>(R.id.title).text = item.musicName
//        }
//    }
//}