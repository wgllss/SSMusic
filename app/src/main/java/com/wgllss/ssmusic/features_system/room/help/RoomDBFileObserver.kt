package com.wgllss.ssmusic.features_system.room.help

import android.content.Context
import android.os.FileObserver
import com.wgllss.ssmusic.core.ex.logE
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RoomDBFileObserver(val context: Context, path: String) : FileObserver(path) {


    override fun onEvent(event: Int, path: String?) {
        GlobalScope.launch {
            when (event) {
                ACCESS -> {}
                ATTRIB -> {}
                CLOSE_NOWRITE -> {}
                CLOSE_WRITE -> {}
                CREATE -> {
                    logE("RoomDBFileObserver path :${path} CREATE")
                }
                DELETE, MOVED_FROM, 1073741888 -> {
//                    path?.takeIf {
//                        it.contains(".clz_db") or it.contains(".android")
//                    }?.let {
//                        val injectDataStoreA = InitializerEntryPoint.resolve(context).injectDataStore().get()
//                        injectDataStoreA.putData(GlobleSettings.IS_ROOM_INIT_KEY, false)
//                        //目录被删除重新创建
//                        FileUtils.getDBPath(".clz_db")
//                    }
                }
                DELETE_SELF -> {}
                MODIFY -> {}
                MOVE_SELF -> {}
                MOVED_TO -> {}
                OPEN -> {}
                else -> { //ALL_EVENTS ： 包括上面的所有事件
                }
            }
        }
    }
}