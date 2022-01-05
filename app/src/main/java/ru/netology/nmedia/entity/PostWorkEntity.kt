package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val postId : Long,
    val author :String,
    val authorId : Long,
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
        this.id,
        this.author,
        this.authorId,
        this.authorAvatar,
        this.published,
        this.content,
        this.likedByMe,
        this.likes,
        this.isRead,
        Attachment(this.uri ?: "", AttachmentType.IMAGE),
        ownedByMe
    )

    companion object {
        fun fromDto(dto : Post) =
            PostWorkEntity(
                0L,
                dto.id,
                dto.author,
                dto.authorId,
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
