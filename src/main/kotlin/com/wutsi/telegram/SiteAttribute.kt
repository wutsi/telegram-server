package com.wutsi.telegram

enum class SiteAttribute(val urn: String) {
    ENABLED("urn:attribute:wutsi:telegram:enabled"),
    TOKEN("urn:attribute:wutsi:telegram:token"),
    CHAT_ID("urn:attribute:wutsi:telegram:chat-id"),
    BITLY_ACCESS_TOKEN("urn:attribute:wutsi:bitly:access-token")
}
