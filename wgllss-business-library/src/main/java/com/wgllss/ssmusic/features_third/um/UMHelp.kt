package com.wgllss.ssmusic.features_third.um
//
import android.content.Context
//import com.umeng.commonsdk.UMConfigure
//import com.wgllss.ssmusic.BuildConfig
//import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch

object UMHelp {
    const val APP_KEY = "634d040505844627b5672f2f"
    const val APP_MASTER_SECRET = "6y2akwgydlplnewzdhqczfmld5kan6xo"
    const val UMENG_MESSAGE_SECRET = "163045f64d1a5f94f8e50cbf8bb81f65"

    fun umInit(context: Context) {
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                UMConfigure.preInit(context, APP_KEY, "umeng");
//                UMConfigure.setLogEnabled(BuildConfig.DEBUG)
//                if (MMKVHelp.getUmInit() == 1) {
//                    UMConfigure.init(context, APP_KEY, "umeng", UMConfigure.DEVICE_TYPE_PHONE, UMENG_MESSAGE_SECRET)
//                } else {
//                    MMKVHelp.setUmInit()
//                    UMConfigure.submitPolicyGrantResult(context, true)
//                    /*** 友盟sdk正式初始化*/
//                    /*** 友盟sdk正式初始化 */
//                    UMConfigure.init(context, APP_KEY, "umeng", UMConfigure.DEVICE_TYPE_PHONE, UMENG_MESSAGE_SECRET)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }
}