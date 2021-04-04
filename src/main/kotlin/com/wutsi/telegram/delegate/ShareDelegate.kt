package com.wutsi.telegram.`delegate`

import com.wutsi.site.SiteApi
import com.wutsi.site.dto.Site
import com.wutsi.story.StoryApi
import com.wutsi.story.dto.Story
import com.wutsi.telegram.AttributeUrn
import com.wutsi.telegram.dao.ShareRepository
import com.wutsi.telegram.entity.ShareEntity
import com.wutsi.telegram.service.bitly.BitlyUrlShortener
import com.wutsi.telegram.service.t.SendMessageResponse
import com.wutsi.telegram.service.t.TelegramClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class ShareDelegate(
    @Autowired private val siteApi: SiteApi,
    @Autowired private val storyApi: StoryApi,
    @Autowired private val telegram: TelegramClient,
    @Autowired private val dao: ShareRepository,
    @Autowired private val bitly: BitlyUrlShortener
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareDelegate::class.java)
    }

    @Transactional
    public fun invoke(storyId: Long) {
        val story = storyApi.get(storyId).story
        val site = siteApi.get(1).site
        if (!supportsTelegram(site))
            return

        val response = share(story, site)
        if (response != null) {
            save(story, site, response)
        }
    }

    private fun share(story: Story, site: Site): SendMessageResponse? {
        val token = token(site)
        val chatId = chatId(site)
        if (token != null && chatId != null) {
            val text = text(story, site)
            return telegram.sendMessage(text, chatId, token)
        }
        return null
    }

    private fun save(story: Story, site: Site, response: SendMessageResponse) {
        try {
            dao.save(
                ShareEntity(
                    storyId = story.id,
                    siteId = site.id,
                    telegramChatId = chatId(site)!!,
                    telegramMessageId = response.result?.message_id,
                    success = response.ok,
                    errorCode = response.error_code,
                    errorMessage = response.description
                )
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to store the share information", ex)
        }
    }

    private fun text(story: Story, site: Site): String {
        val url = bitly.shorten("${site.websiteUrl}${story.slug}?utm_source=telegram", site)
        return "${story.title} $url"
    }

    private fun supportsTelegram(site: Site): Boolean =
        site.attributes.find { AttributeUrn.ENABLED.urn == it.urn }?.value == "true"

    private fun chatId(site: Site): String? =
        site.attributes.find { AttributeUrn.CHAT_ID.urn == it.urn }?.value

    private fun token(site: Site): String? =
        site.attributes.find { AttributeUrn.TOKEN.urn == it.urn }?.value
}
