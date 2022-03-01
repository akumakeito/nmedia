package ru.netology.nmedia.repository

import androidx.paging.*
import ru.netology.nmedia.ApiError
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator (
            private val apiService: ApiService,
            private val dao: PostDao
            ) : RemoteMediator<Int, PostEntity>() {


    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response =  when (loadType) {
                LoadType.REFRESH -> apiService.getLatest(state.config.initialLoadSize)
                LoadType.APPEND -> {
                    val lastId = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    apiService.getAfter(lastId, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    val firstId = state.firstItemOrNull()?.id ?: return MediatorResult.Success(false)
                    apiService.getBefore(firstId, state.config.pageSize)
                }
            }

            if(!response.isSuccessful) {
                throw ApiError(response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.message()
            )

            dao.insert(body.map(PostEntity::fromDto))
            return MediatorResult.Success(body.isEmpty())
        } catch (e : Exception) {
            return MediatorResult.Error(e)
        }
    }
}