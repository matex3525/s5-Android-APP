package com.example.s5app.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context
import com.example.s5app.network.ConnectivityService
import com.example.s5app.network.CupidApiRepository
import com.example.s5app.network.CupidApiRepositoryImpl
import com.example.s5app.network.CupidApiService
import com.example.s5app.use_case.AddPhotoToEventUseCase
import com.example.s5app.use_case.CreateEventUseCase
import com.example.s5app.use_case.GetEventUseCase
import com.example.s5app.use_case.EventUseCases
import com.example.s5app.use_case.GetPhotosForGivenEventUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL =
    "http://10.0.2.2:8080"

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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideCupidApiService(retrofit: Retrofit): CupidApiService {
        return retrofit.create(CupidApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCupidApiRepository(api: CupidApiService): CupidApiRepository {
        return CupidApiRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideEventUseCases(repository: CupidApiRepository): EventUseCases {
        return EventUseCases(
            getEvent = GetEventUseCase(repository),
            createEvent = CreateEventUseCase(repository),
            getPhotosForGivenEvent = GetPhotosForGivenEventUseCase(repository),
            addPhotoToEvent = AddPhotoToEventUseCase(repository)
        )
    }
}