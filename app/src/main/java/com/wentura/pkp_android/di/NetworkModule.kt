package com.wentura.pkp_android.di

import com.wentura.pkp_android.api.BilkomService
import com.wentura.pkp_android.api.KoleoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideBilkomService(): BilkomService {
        return BilkomService.create()
    }

    @Singleton
    @Provides
    fun provideKoleoService(): KoleoService {
        return KoleoService.create()
    }
}
