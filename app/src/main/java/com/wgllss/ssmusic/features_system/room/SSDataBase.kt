package com.wgllss.ssmusic.features_system.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wgllss.ssmusic.features_system.room.dao.MusicDao
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean

@Database(entities = [MusicTabeBean::class], version = 1, exportSchema = false)
abstract class SSDataBase : RoomDatabase() {

    abstract fun musicDao(): MusicDao
}