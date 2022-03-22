package ru.netology.nmedia.repository


import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.ApiError
import ru.netology.nmedia.AppError
import ru.netology.nmedia.NetworkError
import ru.netology.nmedia.UnknownAppError
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    remoteMediator: PostRemoteMediator
) : PostRepository {


    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = postDao::pagingSource,
        remoteMediator = remoteMediator
    ).flow
        .map { it.map(PostEntity::toDto)}


    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            val body = response.body() ?: throw ApiError(response.message())
            if (postDao.isEmpty()) {
                postDao.insert(body.toEntity(false))
                postDao.readNewPost()
            }
            if (body.size > postDao.countPosts()) {
                val notInRoomPosts = body.takeLast(body.size - postDao.countPosts())
                postDao.insert(notInRoomPosts.toEntity(true))
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }


    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            val body = response.body() ?: throw ApiError(response.message())
            postDao.insert(PostEntity.fromDto(body))
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

            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            val body = response.body() ?: throw ApiError(response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun unlikeById(id: Long) {
        try {
            val response = apiService.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            val body = response.body() ?: throw ApiError(response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }


    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            postDao.removeById(id)
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

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            return response.body() ?: throw ApiError(response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }


    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }

            val body = response.body() ?: throw ApiError(response.message())
            postDao.insert(body.toEntity(false))
            val newPosts = postDao.getNewer()
            emit(newPosts.size)
        }
    }
        .catch { e -> AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun readPosts() {
        postDao.readNewPost()
    }


}