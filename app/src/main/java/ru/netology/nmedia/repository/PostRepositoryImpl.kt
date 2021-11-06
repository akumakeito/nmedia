package ru.netology.nmedia.repository


import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.ApiError
import ru.netology.nmedia.AppError
import ru.netology.nmedia.NetworkError
import ru.netology.nmedia.UnknownAppError
//import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import java.io.IOException
import java.lang.Exception

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {


    override val data: Flow<List<Post>> = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)


    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (dao.isEmpty()) {
                dao.insert(body.toEntity(false))
                dao.readNewPost()
            }
            if (body.size > dao.countPosts()) {
                val notInRoomPosts = body.takeLast(body.size - dao.countPosts())
                dao.insert(notInRoomPosts.toEntity(true))
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }


    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun saveWithAttachment(post: Post, media: MediaUpload) {
        try {
            val uploadedMedia = upload(media)
            val postWithAttachment = post.copy(attachment = Attachment(uploadedMedia.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun likeById(id: Long) {
        try {

            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun unlikeById(id: Long) {
        try {
            val response = PostsApi.service.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }


    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun upload(uploadedMedia: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", uploadedMedia.file.name, uploadedMedia.file.asRequestBody()
            )

            val response = PostsApi.service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }


    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity(false))
            val newPosts = dao.getNewer()
            emit(newPosts.size)
        }
    }
        .catch { e -> AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun readPosts() {
        dao.readNewPost()
    }

    override suspend fun signIn() : AuthState {
        //TODO hardcode

        val response = PostsApi.service.updateUser("student", "secret")

        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }

        return response.body() ?: throw ApiError(response.code(), response.message())
    }

//    override suspend fun singOut() : AuthState {
//
//        val response = PostsApi.service.updateUser("", "")
//
//        if (!response.isSuccessful) {
//            throw ApiError(response.code(), response.message())
//        }
//
//        return response.body() ?: throw ApiError(response.code(), response.message())
//    }


}