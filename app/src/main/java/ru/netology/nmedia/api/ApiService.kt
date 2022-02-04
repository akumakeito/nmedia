package ru.netology.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserKey
import ru.netology.ru.netology.nmedia.dto.PushToken

interface ApiService {
    @GET("posts")
    suspend fun getAll() : Response<List<Post>>

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken) : Response<Unit>

    @POST("posts")
    suspend fun save(@Body post: Post) : Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id : Long) : Response<Unit>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id : Long) : Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id : Long) : Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id : Long) : Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media : MultipartBody.Part) : Response<Media>
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login : String, @Field("pass") pass : String) : Response<UserKey>
}