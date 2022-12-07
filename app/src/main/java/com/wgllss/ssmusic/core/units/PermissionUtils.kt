package com.wgllss.ssmusic.core.units

import android.app.AppOpsManager
import android.content.Context
import  android.os.Process
import com.wgllss.ssmusic.core.ex.logE
import java.lang.reflect.Method


object PermissionUtils {

    fun isAllowed(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
        try {
            val op = 10021
            val method: Method = ops!!.javaClass.getMethod("checkOpNoThrow", *arrayOf<Class<*>?>(Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java))
            val result = method.invoke(ops!!, op, Process.myUid(), context.packageName) as Int
            return result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            logE("not support")
        }
        return false
    }
}