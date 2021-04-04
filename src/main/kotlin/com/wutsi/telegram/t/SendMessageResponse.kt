package com.wutsi.telegram.t

data class SendMessageResponse(
    val ok: Boolean = false,
    val result: Message? = null,
    val error_code: Int? = null,
    val description: String? = null
)
