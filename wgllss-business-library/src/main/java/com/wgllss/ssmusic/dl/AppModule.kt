package com.wgllss.ssmusic.dl
//
//import android.content.Context
//import androidx.lifecycle.ViewModelProvider
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.sqlite.db.SupportSQLiteDatabase
//import com.wgllss.core.app.CommonApplicationProxy
//import com.wgllss.core.ex.logE
//import com.wgllss.ssmusic.features_system.room.help.RoomDBMigration
//import com.wgllss.ssmusic.datasource.net.HeaderInterceptor
//import com.wgllss.ssmusic.datasource.net.MusiceApi
//import com.wgllss.ssmusic.features_system.app.AppViewModel
//import com.wgllss.ssmusic.features_system.app.AppViewModelFactory
//import com.wgllss.ssmusic.features_system.room.SSDataBase
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import okhttp3.Cache
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import java.util.concurrent.TimeUnit
//import javax.inject.Singleton
//
//@InstallIn(SingletonComponent::class)
//@Module
//class AppModule {
//    private val base_url = "https://www.hifini.com/"
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(@ApplicationContext application: Context): Retrofit {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//        val timeout = 30000L
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(HeaderInterceptor())
//            .addInterceptor(logging)
////            .addInterceptor(RetrofitClient.BaseUrlInterceptor())
//            .callTimeout(timeout, TimeUnit.MILLISECONDS)
//            //设置连接超时
//            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
//            //设置从主机读信息超时
//            .readTimeout(timeout, TimeUnit.MILLISECONDS)
//            //设置写信息超时
//            .writeTimeout(timeout, TimeUnit.MILLISECONDS)
//            .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
//            .cache(Cache(application.cacheDir, 50 * 1024 * 1024)) //10M cache
//            .build();
//        return Retrofit.Builder()
//            .client(okHttpClient)
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .baseUrl(base_url)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideApiService(retrofit: Retrofit): MusiceApi = retrofit.create(MusiceApi::class.java)
//
//    @Provides
//    @Singleton
//    fun provideAppViewModel(factory: AppViewModelFactory) = ViewModelProvider(CommonApplicationProxy.viewModelStore, factory).get(AppViewModel::class.java)
//
//    @Provides
//    @Singleton
//    fun provideSSMusicDatabase(@ApplicationContext context: Context, roomDBMigration: RoomDBMigration): SSDataBase {
//        val builder = Room.databaseBuilder(context, SSDataBase::class.java, "ssmusic_db")
//        val migrations = roomDBMigration.createMigration()
//        migrations?.takeIf {
//            it.isNotEmpty()
//        }?.let {
//            builder.addMigrations(*it)
//        }
//        builder.addCallback(object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                logE("RoomDatabase onCreate")
//            }
//
//            override fun onOpen(db: SupportSQLiteDatabase) {
//                super.onOpen(db)
//                logE("RoomDatabase onOpen")
//            }
//
//            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
//                super.onDestructiveMigration(db)
//                logE("RoomDatabase onDestructiveMigration")
//            }
//        })
//        return builder.build()
//    }
//}