package ru.netology.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.lang.Exception

class DeletePostWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: PostRepository
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val name = "ru.netology.work.DeletePostsWorker"
        const val postKey = "postId"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }

        return try {
            repository.removeById(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

}

class DeletePostWorkerFactory(
    private val repository: PostRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        DeletePostWorker::class.java.name ->
            DeletePostWorker(appContext,workerParameters,repository)
        else -> null
    }
}