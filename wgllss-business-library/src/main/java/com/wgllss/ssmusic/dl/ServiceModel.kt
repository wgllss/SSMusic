package com.wgllss.ssmusic.dl

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@InstallIn(ServiceComponent::class)
@Module
class ServiceModel {

    @Provides
    @ServiceScoped
    fun provideNotificationManager(@ApplicationContext application: Context): NotificationManagerCompat = NotificationManagerCompat.from(application)
}