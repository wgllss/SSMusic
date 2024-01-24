package com.wgllss.ssmusic.features_ui.page.activation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.flowOnIOAndCatchAAndCollect
import com.wgllss.core.units.StatusBarUtil.setStatusBarTranslucent
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.BarCodeUtils
import com.wgllss.ssmusic.core.units.DeviceIdUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ActiveActivity : FragmentActivity() {

    private lateinit var img_sn: ImageView
    private lateinit var txt_info: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslucent(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active)
        findViewById<ImageView>(R.id.img_back).setOnClickListener {
            finishActivity()
        }
        img_sn = findViewById(R.id.img_sn)
        txt_info = findViewById(R.id.txt_info)
        window.setBackgroundDrawable(null)//去掉主题背景颜色
        val activationType = intent?.getIntExtra("activationType", 0)
        txt_info.text = "你的手机设备未激活授权\n当前${if (activationType == -1) "还在体验期内" else "体验已经到期"}"
        lifecycleScope.launch {
            flow {
                emit(BarCodeUtils.writeQR(DeviceIdUtil.getDeviceId(true), "utf-8", 200, 200)!!)
            }.catch { it.printStackTrace() }
                .flowOn(Dispatchers.IO)
                .collect {
                    img_sn.setImageBitmap(it)
                }
        }

        img_sn.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", DeviceIdUtil.getDeviceId(true))
            clipboardManager.setPrimaryClip(clip)
        }
    }
}