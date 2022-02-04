package ru.netology.nmedia.service

import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoogleApiAvailabilityModule {

    @Provides
    @Singleton
    fun provideGoogleApiAvailability() : GoogleApiAvailability = GoogleApiAvailability.getInstance()
}