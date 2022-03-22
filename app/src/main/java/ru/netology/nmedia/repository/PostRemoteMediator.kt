package ru.netology.nmedia.repository

import androidx.paging.*
import androidx.room.withTransaction
import kotlinx.coroutines.flow.takeWhile
import ru.netology.nmedia.ApiError
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val db: AppDB
            ) : RemoteMediator<Int, PostEntity>() {

    override suspend fun initialize(): InitializeAction = if (postDao.isEmpty()) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }


    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response =  when (loadType) {
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id == null) {
                        apiService.getLatest(state.config.initialLoadSize)
                    } else {
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                    //val id = postRemoteKeyDao.max() ?:
                    //apiService.getAfter(id, state.config.pageSize)
                }
            }

            if(!response.isSuccessful) {
                throw ApiError(
                    response.message()
                )

            }

            val body = response.body() ?: throw ApiError(
                response.message()
            )

            if (body.isEmpty()) {
                return MediatorResult.Success(false)
            }

            db.withTransaction {
                when(loadType) {
                    LoadType.REFRESH -> {
                       if (postDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                    listOf(
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.AFTER,
                                            id = body.first().id
                                        ),
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.BEFORE,
                                            id = body.last().id
                                        )
                                    )
                            )
                        } else {
                            postRemoteKeyDao.insert(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id
                                    )
                            )
                        }

                    }
//                    LoadType.PREPEND ->
//                        postRemoteKeyDao.insert(
//                            PostRemoteKeyEntity(
//                                type = PostRemoteKeyEntity.KeyType.AFTER,
//                                id = body.first().id
//                            )
//                    )
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id
                            )
                        )
                    }
                }
                postDao.insert(body.map(PostEntity::fromDto))
            }


            return MediatorResult.Success(body.isEmpty())
        } catch (e : Exception) {
            return MediatorResult.Error(e)
        }
    }
}