package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.datasource.netbean.rank.KRankExBean

class KHomeItem3Adapter : BaseRecyclerAdapter<KRankExBean>() {
    private val img = 1
    private val txt1 = 2
    private val txt2 = 3
    private val txt3 = 4

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val frameLayout = FrameLayout(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(120f).toInt())
        }
        val image = ShapeableImageView(parent.context).apply {
            id = img
            val size = context.getIntToDip(90f).toInt()
            val lp = LinearLayout.LayoutParams(size, size)
            lp.gravity = Gravity.TOP or Gravity.LEFT
            layoutParams = lp
        }
        frameLayout.addView(image)

        val textView1 = TextView(context).apply {
            id = txt1
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getIntToDip(25f).toInt())
            lp.leftMargin = context.getIntToDip(100f).toInt()
            maxLines = 1
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = lp
        }
        frameLayout.addView(textView1)

        val textView2 = TextView(context).apply {
            id = txt2
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getIntToDip(25f).toInt())
            lp.leftMargin = context.getIntToDip(100f).toInt()
            lp.topMargin = context.getIntToDip(30f).toInt()
            maxLines = 1
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = lp
        }
        frameLayout.addView(textView2)

        val textView3 = TextView(context).apply {
            id = txt3
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getIntToDip(25f).toInt())
            lp.leftMargin = context.getIntToDip(100f).toInt()
            lp.topMargin = context.getIntToDip(60f).toInt()
            maxLines = 1
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = lp
        }
        frameLayout.addView(textView3)
        return BaseBindingViewHolder(frameLayout)
    }

    override fun onBindItem(context: Context, item: KRankExBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<ImageView>(img).loadUrl(item.imgUrl)
        item.topBean[0]?.run {
            holder.itemView.findViewById<TextView>(txt1).text = "$no $musicName  $author"
        }
        item.topBean[1]?.run {
            holder.itemView.findViewById<TextView>(txt2).text = "$no $musicName  $author"
        }
        item.topBean[2]?.run {
            holder.itemView.findViewById<TextView>(txt3).text = "$no $musicName  $author"
        }
    }
}