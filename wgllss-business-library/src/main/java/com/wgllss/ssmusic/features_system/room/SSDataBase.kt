package com.wgllss.ssmusic.features_system.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wgllss.core.ex.logE
import com.wgllss.ssmusic.datasource.net.RetrofitUtils
import com.wgllss.ssmusic.features_system.room.dao.MusicDao
import com.wgllss.ssmusic.features_system.room.help.RoomDBMigration
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean

@Database(entities = [MusicTabeBean::class], version = 1, exportSchema = false)
abstract class SSDataBase : RoomDatabase() {

    companion object {
        @Volatile
        private var instance: SSDataBase? = null
        fun getInstance(context: Context, roomDBMigration: RoomDBMigration): SSDataBase {
            if (instance == null) {
                synchronized(SSDataBase::class.java) {
                    if (instance == null) {
                        val builder = Room.databaseBuilder(context, SSDataBase::class.java, "ssmusic_db")
                        val migrations = roomDBMigration.createMigration()
                        migrations?.takeIf {
                            it.isNotEmpty()
                        }?.let {
                            builder.addMigrations(*it)
                        }
                        builder.addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                logE("RoomDatabase onCreate")
                            }

                            override fun onOpen(db: SupportSQLiteDatabase) {
                                super.onOpen(db)
                                logE("RoomDatabase onOpen")
                            }

                            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                                super.onDestructiveMigration(db)
                                logE("RoomDatabase onDestructiveMigration")
                            }
                        })
                        instance = builder.build()
                    }
                }
            }
            return instance!!
        }
    }

    abstract fun musicDao(): MusicDao
}