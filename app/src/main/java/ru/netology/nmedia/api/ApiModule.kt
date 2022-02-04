package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.TokenRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL
    }

    @Provides
    @Singleton
    fun provideLogging() : Interceptor = HttpLoggingInterceptor().apply {
        if(BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkhttp(
        logging : Interceptor,
        tokenRepository : TokenRepository
    ) = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            tokenRepository.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttp: OkHttpClient
    ) =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp)
            .baseUrl(BASE_URL)
            .build()

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ) : ApiService = retrofit.create(ApiService::class.java)

}