package com.wentura.pkp_android.di

import com.wentura.pkp_android.api.BilkomService
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
}
