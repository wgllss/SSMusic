package com.wgllss.ssmusic.core.units

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.*


object BarCodeUtils {

    /**
     * 生成二维码
     *
     * @param content   数据内容
     * @param coding    编码如utf-8
     * @param imgWidth  生成图片宽度
     * @param imgHeight 生成图片高度
     * @return Bitmap对象
     * @throws WriterException
     */
    @Throws(WriterException::class)
    fun writeQR(content: String, coding: String, imgWidth: Int, imgHeight: Int): Bitmap? {
        val hints = Hashtable<EncodeHintType, String?>()
        hints[EncodeHintType.CHARACTER_SET] = coding
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, imgWidth, imgHeight, hints)
        return BitMatrixToBitmap(bitMatrix)
    }

    /**
     * BitMatrix转换成Bitmap
     *
     * @param matrix
     * @return
     */
    private fun BitMatrixToBitmap(matrix: BitMatrix): Bitmap? {
        val WHITE = -0x1
        val BLACK = -0x1000000
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) BLACK else WHITE
            }
        }
        return createBitmap(width, height, pixels)
    }

    /**
     * 生成Bitmap
     *
     * @param width
     * @param height
     * @param pixels
     * @return
     */
    private fun createBitmap(width: Int, height: Int, pixels: IntArray): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * 将字节数组转换为ImageView可调用的Bitmap对象
     * @param bytes
     * @param opts 转换属性设置
     * @return
     **/
    fun getPicFromBytes(bytes: ByteArray?, opts: BitmapFactory.Options?): Bitmap? {
        return if (bytes != null)
            if (opts != null)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
            else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        else null
    }
}