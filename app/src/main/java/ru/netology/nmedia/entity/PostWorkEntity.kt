package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val postId :Long,
    val author :String,
    val authorAvatar : String,
    val content : String,
    val published : Long,
    val isRead : Boolean,
    val likedByMe : Boolean,
    val likes : Int,
    val ownedByMe : Boolean,

    @Embedded
    var attachment : AttachmentEmbeddable?,
    var uri : String? = null
) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        published,
        content,
        likedByMe,
        likes,
        isRead,
        attachment?.toDto(),
        ownedByMe
    )

    companion object {
        fun fromDto(dto : Post) =
            PostWorkEntity(
                0L,
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.isRead,
                dto.likedByMe,
                dto.likes,
                dto.ownedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}
