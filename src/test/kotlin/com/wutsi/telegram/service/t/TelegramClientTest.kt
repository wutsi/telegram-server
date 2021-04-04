package com.wutsi.telegram.service.t

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

internal class TelegramClientTest {
    lateinit var rest: RestTemplate

    lateinit var client: TelegramClient

    @BeforeEach
    fun setUp() {
        rest = mock()
        client = TelegramClient(rest)
    }

    @Test
    fun sendMessage() {
        val url = "https://api.telegram.org/bot00:11/sendMessage?chat_id=@foo&text=Yo"
        val response = ResponseEntity.ok(SendMessageResponse())
        doReturn(response).whenever(rest).getForEntity(anyString(), eq(SendMessageResponse::class.java))

        client.sendMessage("Yo", "@foo", "00:11")

        verify(rest).getForEntity(url, SendMessageResponse::class.java)
    }
}
