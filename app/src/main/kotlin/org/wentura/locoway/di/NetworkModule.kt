package org.wentura.locoway.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.wentura.locoway.api.BilkomService
import org.wentura.locoway.api.KoleoService

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
