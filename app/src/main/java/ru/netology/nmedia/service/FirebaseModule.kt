package ru.netology.nmedia.service

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebase() : FirebaseMessaging = FirebaseMessaging.getInstance()
}