package com.wutsi.telegram.`delegate`

import com.wutsi.site.SiteApi
import com.wutsi.site.dto.Site
import com.wutsi.story.StoryApi
import com.wutsi.story.dto.Story
import com.wutsi.telegram.AttributeUrn
import com.wutsi.telegram.t.TelegramApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.Long

@Service
public class ShareDelegate(
    @Autowired private val siteApi: SiteApi,
    @Autowired private val storyApi: StoryApi,
    @Autowired private val telegramApi: TelegramApi
) {
    public fun invoke(storyId: Long) {
        val story = storyApi.get(storyId).story
        val site = siteApi.get(1).site
        if (!supportsTelegram(site))
            return

        val token = token(site)
        val chatId = chatId(site)
        if (token != null && chatId != null) {
            val text = text(story, site)
            telegramApi.sendMessage(text, chatId, token)
        }
    }

    private fun text(story: Story, site: Site): String =
        "${story.title} ${site.websiteUrl}?utm_source=telegram"

    private fun supportsTelegram(site: Site): Boolean =
        site.attributes.find { AttributeUrn.ENABLED.urn == it.urn }?.value == "true"

    private fun chatId(site: Site): String? =
        site.attributes.find { AttributeUrn.CHAT_ID.urn == it.urn }?.value

    private fun token(site: Site): String? =
        site.attributes.find { AttributeUrn.TOKEN.urn == it.urn }?.value
}
