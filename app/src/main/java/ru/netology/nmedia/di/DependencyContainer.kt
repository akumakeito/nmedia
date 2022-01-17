package ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.repository.UserRepositoryImpl

class DependencyContainer private constructor(context : Context) {
    private val db  =  Room.databaseBuilder(context, AppDB::class.java,"app.db")
        .fallbackToDestructiveMigration()
        .build()


    private val logging = HttpLoggingInterceptor().apply {
        if(BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val userRepository = UserRepositoryImpl(
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    )

    private val okhttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            userRepository.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okhttp)
        .baseUrl(BASE_URL)
        .build()

    private val service : ApiService = retrofit.create()
    private val repository : PostRepository = PostRepositoryImpl(db.postDao(),db.postWorkDao(), service)

    private val appAuth = AppAuth(service, userRepository)




    companion object {

        private const val BASE_URL = BuildConfig.BASE_URL

        @Volatile
        private var instance : DependencyContainer? = null

        fun init(context: Context) {
            instance ?: synchronized(this) {
                instance ?: DependencyContainer(context).also { instance = it}
            }
        }

    }
}
