package com.wutsi.blog.channel.service.telegram.model

data class Message(
    val message_id: Long = -1,
    val date: Long = -1,
    val text: String? = null
)
