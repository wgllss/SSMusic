package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.features_ui.page.home.adapter.SingersAdapter

class GroupItemDecoration(val adapter: SingersAdapter) : RecyclerView.ItemDecoration() {


    /**
     * 顶部的大小
     */
    private var topHeight = dp2px(35)

    /**
     * 顶部背景画笔
     */
    private var topPaint: Paint = Paint()

    /**
     *顶部文字画笔
     */
    private var topTextPaint = Paint()

    /**
     * 顶部文字大小
     */
    private var topTextSize = dp2px(18)

    /**
     * 顶部文字边距
     */
    private var topTextPadding = dp2px(15)
    private var topTextRect: Rect = Rect()


    /**
     * 分组头部的大小
     */
    private var headHeight = dp2px(35)

    /**
     * 分组头部背景画笔
     */
    private var headPaint: Paint = Paint()

    /**
     *分组头部文字画笔
     */
    private var headTextPaint = Paint()

    /**
     * 分组头部文字大小
     */
    private var headTextSize = dp2px(18)

    /**
     * 分组头部文字边距
     */
    private var headTextPadding = dp2px(15)
    private var headTextRect: Rect = Rect()
    private var colorHeader = 0
    private var colorHeaderTop = 0


    /**
     * 分隔线画笔
     */
    private var mPaint: Paint = Paint()


    /**
     * 初始化画笔
     */
    init {
        topPaint.isAntiAlias = true
        topPaint.strokeWidth = 5f
        topPaint.style = Paint.Style.FILL
        topPaint.color = colorHeaderTop

        topTextPaint.color = Color.WHITE
        topTextPaint.style = Paint.Style.FILL
        topTextPaint.isAntiAlias = true
        topTextPaint.textSize = topTextSize.toFloat()

        headPaint.isAntiAlias = true
        headPaint.strokeWidth = 5f
        headPaint.style = Paint.Style.FILL
        headPaint.color = colorHeader

        headTextPaint.color = Color.BLACK
        headTextPaint.style = Paint.Style.FILL
        headTextPaint.isAntiAlias = true
        headTextPaint.textSize = headTextSize.toFloat()

        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 1f
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = Color.parseColor("#30000000")
    }

    fun setColor(colorHeader: Int, colorHeaderTop: Int) {
        this.colorHeader = colorHeader
        this.colorHeaderTop = colorHeaderTop
        topPaint.color = colorHeaderTop
        headPaint.color = colorHeader
    }

    /**
     * onDraw先绘制，然后在轮到item,最后是onDrawOver
     * 绘制分组的头部
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
//        if (parent.adapter is UserAdapter) {
//            val adapter =adapter
        val count = parent.childCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        //遍历所有子view
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            val childPosition = parent.getChildAdapterPosition(view)

            //在paddingTop范围内绘制
            if (view.top - headHeight >= parent.paddingTop) {
                //如果是分组的头部
                if (adapter.isGroupHead(childPosition)) {
                    val groupName = adapter.getGroupName(childPosition)

                    //绘制头部的背景
                    val rect = Rect(left, view.top - headHeight, right, view.top)
                    c.drawRect(rect, headPaint)

                    //绘制头部文字
                    headTextPaint.getTextBounds(groupName, 0, groupName.length, headTextRect)
                    c.drawText(
                        groupName,
                        (left + headTextPadding).toFloat(),
                        (view.top - (headHeight - headTextRect.height()) / 2).toFloat(),
                        headTextPaint
                    )
                }
                //如果不是头部，就绘制分隔线
                else {
                    val rect = Rect(left, view.top - 1, right, view.top)
                    c.drawRect(rect, mPaint)
                }
            }
        }
//        }
    }

    /**
     * 绘制吸顶效果
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            //找到RecyclerView第一个显示的view的position
            val position = layoutManager.findFirstVisibleItemPosition()
            //通过viewHolder获取itemView
            val childView = parent.findViewHolderForAdapterPosition(position)?.itemView

            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val top = parent.paddingTop

            childView?.let {
                //如果第一个可见itemView的下一个是组的头部，就把吸顶的顶上去
                if (adapter.isGroupHead(position + 1)) {
                    //绘制吸顶头部的背景,bottom会随着上滑越来越小
                    val bottom = Math.min(topHeight, childView.bottom - top)
                    val rect = Rect(left, top, right, top + bottom)
                    c.drawRect(rect, topPaint)

                    //绘制吸顶的头部文字
                    val groupName = adapter.getGroupName(position)
                    topTextPaint.getTextBounds(groupName, 0, groupName.length, topTextRect)

                    //将超出的挡住裁掉
                    val clipRect = Rect(left, top + bottom, right, top)
                    c.clipRect(clipRect)

                    c.drawText(
                        groupName,
                        (left + topTextPadding).toFloat(),
                        (top + bottom - (topHeight - topTextRect.height()) / 2).toFloat(),
                        topTextPaint
                    )
                }
                //如果第一个可见itemView的下一个不是组的头部，就直接绘制吸顶头部
                else {
                    //绘制吸顶头部的背景
                    val rect = Rect(left, top, right, top + topHeight)
                    c.drawRect(rect, topPaint)

                    //绘制吸顶的头部文字
                    val groupName = adapter.getGroupName(position)
                    topTextPaint.getTextBounds(groupName, 0, groupName.length, topTextRect)

                    c.drawText(
                        groupName,
                        (left + topTextPadding).toFloat(),
                        (top + topHeight - (topHeight - topTextRect.height()) / 2).toFloat(),
                        topTextPaint
                    )
                }
            }
//            }
        }
    }

    /**
     * 设置itemView偏移大小
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        //判断分组头
        if (adapter.isGroupHead(position)) {
            outRect.set(0, headHeight, 0, 0)
        }
        //分隔线
        else {
            outRect.set(0, 1, 0, 0)
        }
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}