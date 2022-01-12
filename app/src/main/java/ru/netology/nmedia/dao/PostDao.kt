package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY pid DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE isRead = 0 ORDER BY pid DESC")
    fun getNewer(): List<PostEntity>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty() : Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE pid = :id
        """
    )
    suspend fun likeById(id: Long)

    @Query("""
        UPDATE PostEntity SET
        isRead = 1
        """)
    suspend fun readNewPost()

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun countPosts() : Int

    @Query("DELETE FROM PostEntity WHERE pid = :id")
    suspend fun removeById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts : List<PostEntity>)
}

class Converters {
    @TypeConverter
    fun toAttachmentType(value :String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value : AttachmentType) = value.name
}