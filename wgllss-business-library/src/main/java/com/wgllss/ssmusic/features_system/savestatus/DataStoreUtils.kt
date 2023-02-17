package com.wgllss.ssmusic.features_system.savestatus
//
//import android.content.Context
//import androidx.datastore.preferences.core.*
//import androidx.datastore.preferences.createDataStore
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import java.io.IOException
//
//class DataStoreUtils constructor(context: Context, private val dataStoreName: String) {
//    val dataStore by lazy { context.createDataStore(dataStoreName) }
//
//    /**
//     * 主协程中存入数据
//     */
//    inline fun <reified U : Any> putDataWithSuspend(key: String, value: U) {
//        runBlocking {
//            dataStore.edit { mutablePreferences ->
//                mutablePreferences[preferencesKey<U>(key)] = value
//            }
//        }
//    }
//
//    /**
//     * 存入数据 需要在协程中执行
//     */
//    suspend inline fun <reified U : Any> putData(key: String, value: U) {
//        dataStore.edit { mutablePreferences ->
//            mutablePreferences[preferencesKey<U>(key)] = value
//        }
//    }
//
//    /**
//     * 取数据 需要在协程中执行
//     */
//    suspend inline fun <reified U : Any> getData(key: String, default: U): U {
//        return getDataFlow(key, default).first()
//    }
//
//    /**
//     * 在主协程中取数据
//     */
//    inline fun <reified U : Any> getDataWithSuspend(key: String, default: U): U {
//        val u: U
//        runBlocking {
//            u = getDataFlow(key, default).first()
//        }
//        return u
//    }
//
//
//    inline fun <reified U : Any> getDataFlow(key: String, default: U): Flow<U> {
//        return dataStore.data
//            .catch {
//                if (it is IOException) {
//                    it.printStackTrace()
//                    emit(emptyPreferences())
//                } else {
//                    throw it
//                }
//            }.map {
//                it[preferencesKey(key)] ?: default
//            }
//    }
//
//    /**
//     * 清除数据
//     */
//    fun clearWithSuspend() {
//        runBlocking {
//            dataStore.edit {
//                it.clear()
//            }
//        }
//    }
//
//    suspend fun clear() {
//        dataStore.edit {
//            it.clear()
//        }
//    }
//
//    fun removeKey(key: String, coroutineScope: CoroutineScope) {
//        coroutineScope.launch {
//            dataStore.edit {
//                it.remove(preferencesKey(key))
//            }
//        }
//    }
//
//    fun removeKeyWithSupend(key: String) {
//        runBlocking {
//            dataStore.edit {
//                it.remove(preferencesKey(key))
//            }
//        }
//    }
//
//}