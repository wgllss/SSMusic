package com.wgllss.dynamic.lrclibrary

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils

class LrcEntry(
    val time: Long = 0,
    val text: String = "",
    var secondText: String = ""
) : Comparable<LrcEntry> {
    var staticLayout: StaticLayout? = null

    var offset = Float.MIN_VALUE

    companion object {
        const val GRAVITY_CENTER = 0
        const val GRAVITY_LEFT = 1
        const val GRAVITY_RIGHT = 2
    }


    fun init(paint: TextPaint?, width: Int, gravity: Int) {
        val align: Layout.Alignment = when (gravity) {
            GRAVITY_LEFT -> Layout.Alignment.ALIGN_NORMAL
            GRAVITY_CENTER -> Layout.Alignment.ALIGN_CENTER
            GRAVITY_RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_CENTER
        }
        staticLayout = StaticLayout(getShowText(), paint, width, align, 1f, 0f, false)
        offset = Float.MIN_VALUE
    }

    fun getHeight(): Int {
        return if (staticLayout == null) {
            0
        } else staticLayout!!.height
    }

    private fun getShowText(): String {
        return if (!TextUtils.isEmpty(secondText)) {
            "$text\n$secondText"
        } else {
            text
        }
    }

    override fun compareTo(entry: LrcEntry): Int {
        return if (entry == null) {
            -1
        } else (time - entry.time).toInt()
    }

}