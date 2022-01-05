package ru.netology.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.lang.Exception

class SavePostWorker(
    applicationContext: Context,
    params :WorkerParameters
) : CoroutineWorker (applicationContext, params) {
    companion object{
        const val name = "ru.netology.work.SavePostsWorker"
        const val postKey = "post"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }

        val repository : PostRepository =
            PostRepositoryImpl(
                AppDB.getInstance(context = applicationContext).postDao(),
                AppDB.getInstance(context = applicationContext).postWorkDao()
            )

        return try {
            repository.processWork(id)
            Result.success()
        } catch (e : Exception) {
            Result.failure()
        }

    }
}