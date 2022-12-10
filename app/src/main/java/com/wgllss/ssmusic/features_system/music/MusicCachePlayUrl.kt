package com.wgllss.ssmusic.features_system.music

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicCachePlayUrl @Inject constructor() {

    companion object {
        private const val maxAge: Long = 60 * 60 * 1000//缓存一小时
        private val map by lazy { ConcurrentHashMap<String, String>() }
        private val loadingDates by lazy { ConcurrentHashMap<String, Long>() }
    }


    fun put(key: String, value: String) {
        map[key] = value
        loadingDates[key] = System.currentTimeMillis()
    }

    fun get(key: String): String? {
        loadingDates.takeIf {
            it.containsKey(key) && map.containsKey(key) && map[key]!! != null && it[key]!! != null
        }?.let {
            if ((System.currentTimeMillis() - it[key]!! < maxAge)) {
                return map[key]
            }
            loadingDates.remove(key)
            map.remove(key)
        }
        return null
    }
}