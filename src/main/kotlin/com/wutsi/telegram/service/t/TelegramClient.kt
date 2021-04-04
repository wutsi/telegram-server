package com.wutsi.telegram.service.t

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TelegramClient(
    private val rest: RestTemplate = RestTemplate()
) {
    fun sendMessage(
        text: String,
        chatId: String,
        token: String
    ): SendMessageResponse {
        val path = "/sendMessage?chat_id=$chatId&text=$text"
        return get(path, token, SendMessageResponse::class.java)
    }

    private fun <T> get(path: String, token: String, type: Class<T>): T {
        val url = "https://api.telegram.org/bot$token$path"
        return rest.getForEntity(url, type).body
    }
}
