package com.wgllss.ssmusic.features_ui.page.activation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.units.StatusBarUtil.setStatusBarTranslucent
import com.wgllss.core.widget.CommonToast
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.DeviceIdUtil

class ActiveActivity : FragmentActivity() {

    private lateinit var btn_sn: TextView
    private lateinit var txt_info: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslucent(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active)
        findViewById<ImageView>(R.id.img_back).setOnClickListener {
            finishActivity()
        }
        btn_sn = findViewById(R.id.btn_sn)
        txt_info = findViewById(R.id.txt_info)
        window.setBackgroundDrawable(null)//去掉主题背景颜色
        val activationType = intent?.getIntExtra("activationType", 0)
        txt_info.text = "你的手机设备未激活授权\n当前${if (activationType == -1) "还在体验期内" else "体验已经到期"}"
        btn_sn.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", DeviceIdUtil.getDeviceId(true))
            clipboardManager.setPrimaryClip(clip)
            CommonToast.show("复制成功")
        }
    }
}