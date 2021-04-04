package com.wutsi.telegram.t

data class Message(
    val message_id: Long = -1,
    val date: Long = -1,
    val text: String? = null
)
