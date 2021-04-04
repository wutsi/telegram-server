package com.wutsi.blog.channel.service.telegram

import com.wutsi.blog.channel.service.telegram.model.Message

data class SendMessageResponse(
    val ok: Boolean = false,
    val result: Message = Message()
)
