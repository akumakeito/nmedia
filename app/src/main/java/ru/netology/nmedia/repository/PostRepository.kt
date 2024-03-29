package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post


interface PostRepository {
    val data : Flow<PagingData<Post>>
    suspend fun getAll()
    suspend fun likeById (id : Long)
    suspend fun unlikeById (id : Long)
    suspend fun save (post : Post)
    suspend fun saveWithAttachment(post : Post, media : MediaUpload)
    suspend fun removeById (id: Long)
    suspend fun upload(uploadedMedia : MediaUpload) : Media
    fun getNewerCount(id: Long) :Flow<Int>
    suspend fun readPosts()
}