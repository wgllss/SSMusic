package com.wgllss.ssmusic.dl

import android.content.Context
import com.wgllss.ssmusic.datasource.net.HeaderInterceptor
import com.wgllss.ssmusic.datasource.net.MusiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    private val base_url = "https://www.hifini.com/"

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext application: Context): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val timeout = 30000L
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(logging)
//            .addInterceptor(RetrofitClient.BaseUrlInterceptor())
            .callTimeout(timeout, TimeUnit.MILLISECONDS)
            //设置连接超时
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            //设置从主机读信息超时
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            //设置写信息超时
            .writeTimeout(timeout, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
            .cache(Cache(application.cacheDir, 50 * 1024 * 1024)) //10M cache
            .build();
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(base_url)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): MusiceApi = retrofit.create(MusiceApi::class.java)
}