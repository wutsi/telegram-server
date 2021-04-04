package com.wutsi.telegram.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.site.SiteApi
import com.wutsi.site.dto.Attribute
import com.wutsi.site.dto.GetSiteResponse
import com.wutsi.site.dto.Site
import com.wutsi.story.StoryApi
import com.wutsi.story.dto.GetStoryResponse
import com.wutsi.story.dto.Story
import com.wutsi.telegram.AttributeUrn
import com.wutsi.telegram.t.TelegramClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ShareControllerTest {
    @LocalServerPort
    private val port = 0

    private lateinit var url: String

    private val rest: RestTemplate = RestTemplate()

    @MockBean
    private lateinit var siteApi: SiteApi

    @MockBean
    private lateinit var storyApi: StoryApi

    @MockBean
    private lateinit var telegramClient: TelegramClient

    @BeforeEach
    fun setUp() {
        url = "http://127.0.0.1:$port/v1/telegram/share?story-id={story-id}"
    }

    @Test
    fun `send message to telegram when sharing story-id`() {
        val site = createSite()
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        val story = createStory()
        doReturn(GetStoryResponse(story)).whenever(storyApi).get(123L)

        rest.getForEntity(url, Any::class.java, "123")

        val text = "${story.title} https://www.wutsi.com${story.slug}?utm_source=telegram"
        verify(telegramClient).sendMessage(text, "@test_channel", "000:111")
    }

    @Test
    fun `do not send message to telegram when telegram not enabled`() {
        val site = createSite(
            attributes = listOf(
                Attribute(AttributeUrn.CHAT_ID.urn, "@test_channel"),
                Attribute(AttributeUrn.TOKEN.urn, "000:111")
            )
        )
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        val story = createStory()
        doReturn(GetStoryResponse(story)).whenever(storyApi).get(123L)

        rest.getForEntity(url, Any::class.java, "123")

        val text = "${story.title} https://www.wutsi.com${story.slug}?utm_source=telegram"
        verify(telegramClient, never()).sendMessage(any(), any(), any())
    }

    @Test
    fun `do not send message to telegram when token not enabled`() {
        val site = createSite(
            attributes = listOf(
                Attribute(AttributeUrn.CHAT_ID.urn, "@test_channel"),
                Attribute(AttributeUrn.ENABLED.urn, "true")
            )
        )
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        val story = createStory()
        doReturn(GetStoryResponse(story)).whenever(storyApi).get(123L)

        rest.getForEntity(url, Any::class.java, "123")

        val text = "${story.title} https://www.wutsi.com${story.slug}?utm_source=telegram"
        verify(telegramClient, never()).sendMessage(any(), any(), any())
    }

    @Test
    fun `do not send message to telegram when chat-id not available`() {
        val site = createSite(
            attributes = listOf(
                Attribute(AttributeUrn.ENABLED.urn, "true"),
                Attribute(AttributeUrn.TOKEN.urn, "000:111")
            )
        )
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        val story = createStory()
        doReturn(GetStoryResponse(story)).whenever(storyApi).get(123L)

        rest.getForEntity(url, Any::class.java, "123")

        val text = "${story.title} https://www.wutsi.com${story.slug}?utm_source=telegram"
        verify(telegramClient, never()).sendMessage(any(), any(), any())
    }

    private fun createStory() = Story(
        id = 123L,
        title = "This is a story title",
        slug = "/read/123/this-is-a-story-title"
    )

    private fun createSite(
        attributes: List<Attribute> = listOf(
            Attribute(AttributeUrn.ENABLED.urn, "true"),
            Attribute(AttributeUrn.CHAT_ID.urn, "@test_channel"),
            Attribute(AttributeUrn.TOKEN.urn, "000:111")
        )
    ) = Site(
        id = 1L,
        domainName = "www.wutsi.com",
        websiteUrl = "https://www.wutsi.com",
        attributes = attributes
    )
}
