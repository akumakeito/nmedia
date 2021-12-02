package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val published: Long,
    val content: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    var isRead: Boolean = false,
    val attachment : Attachment? = null,
    val ownedByMe : Boolean = false
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
