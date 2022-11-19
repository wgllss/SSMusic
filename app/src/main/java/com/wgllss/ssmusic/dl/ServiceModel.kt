package com.wgllss.ssmusic.dl

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ServiceComponent::class)
@Module
class ServiceModel {
    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext application: Context) = NotificationManagerCompat.from(application)
}