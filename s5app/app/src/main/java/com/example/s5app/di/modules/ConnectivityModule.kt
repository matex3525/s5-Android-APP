package com.example.s5app.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context
import com.example.s5app.network.ConnectivityService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Zakres życia singletona w całej aplikacji
object ConnectivityModule {
    @Provides
    @Singleton
    fun provideConnectivityService(
        @ApplicationContext context: Context
    ): ConnectivityService {
        return ConnectivityService(context)
    }
}