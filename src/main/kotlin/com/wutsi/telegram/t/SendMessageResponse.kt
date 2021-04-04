package com.wutsi.telegram.t

data class SendMessageResponse(
    val ok: Boolean = false,
    val result: Message = Message()
)
