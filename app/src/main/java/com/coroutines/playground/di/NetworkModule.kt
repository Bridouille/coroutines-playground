package com.coroutines.playground.di

import com.coroutines.playground.BuildConfig
import com.coroutines.playground.network.RickAndMortyEndpoint
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://rickandmortyapi.com/"

    @Provides @Singleton
    fun providesMoshi() = Moshi.Builder().build()

    @Provides @Singleton
    fun provideLoggingInterceptor() : Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton
    fun provideOkHttpClient(loggingInterceptor: Interceptor) : OkHttpClient {
        if (BuildConfig.DEBUG) {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
        return OkHttpClient.Builder()
            .build()
    }

    @Provides @Singleton
    fun providesRetrofit(moshi: Moshi, httpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClient)
            .build()
    }

    @Provides @Singleton
    fun providesRickAndMortyEndpoint(retrofit: Retrofit) = retrofit.create(RickAndMortyEndpoint::class.java)
}