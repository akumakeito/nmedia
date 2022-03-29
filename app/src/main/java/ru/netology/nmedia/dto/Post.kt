package ru.netology.nmedia.dto

sealed interface FeedItem{
    val id : Long
}

data class Ad(
    val image : String,
    override val id: Long
) : FeedItem

data class Post(
    override val id: Long,
    val authorId : Long,
    val author: String,
    val authorAvatar: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    var isRead: Boolean,
    val attachment : Attachment? = null,
    val ownedByMe : Boolean = false
) : FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
