package com.wgllss.ssmusic.dl

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wgllss.ssmusic.core.app.CommonApplicationProxy
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.app.AppViewModelFactory
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.help.RoomDBMigration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppViewModel(factory: AppViewModelFactory) = ViewModelProvider(CommonApplicationProxy.viewModelStore, factory).get(AppViewModel::class.java)

    @Provides
    @Singleton
    fun provideSSMusicDatabase(@ApplicationContext context: Context, roomDBMigration: RoomDBMigration): SSDataBase {
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
        return builder.build()
    }
}