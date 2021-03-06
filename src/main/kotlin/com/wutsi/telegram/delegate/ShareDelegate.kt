package com.wutsi.telegram.`delegate`

import com.wutsi.site.SiteApi
import com.wutsi.site.SiteAttribute
import com.wutsi.site.dto.Site
import com.wutsi.story.StoryApi
import com.wutsi.story.dto.Story
import com.wutsi.telegram.dao.ShareRepository
import com.wutsi.telegram.entity.ShareEntity
import com.wutsi.telegram.service.bitly.BitlyUrlShortenerFactory
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
    @Autowired private val bitly: BitlyUrlShortenerFactory
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareDelegate::class.java)
    }

    @Transactional
    public fun invoke(storyId: Long) {
        val story = storyApi.get(storyId).story
        val site = siteApi.get(story.siteId).site
        if (!supportsTelegram(site)) {
            LOGGER.warn("Site#${story.siteId} doesn't have Telegram enabled")
            return
        }

        try {
            val response = share(story, site)
            if (response != null) {
                save(story, site, response)
            }
        } catch (ex: Exception) {
            save(story, site, ex)
        }
    }

    private fun share(story: Story, site: Site): SendMessageResponse? {
        val token = token(site)
        val chatId = chatId(site)
        if (token != null && chatId != null) {
            val text = text(story, site)
            LOGGER.info("Sharing to $chatId: $text")
            return telegram.sendMessage(text, chatId, token)
        } else {
            LOGGER.warn("Site#${story.siteId} doesn't have Telegram chatId or token configured")
            return null
        }
    }

    private fun save(
        story: Story,
        site: Site,
        ex: Exception
    ) {
        try {
            dao.save(
                ShareEntity(
                    storyId = story.id,
                    siteId = site.id,
                    telegramChatId = chatId(site) ?: "-",
                    telegramMessageId = null,
                    success = false,
                    errorCode = -1,
                    errorMessage = ex.message,
                )
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to store the share information", ex)
        }
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
        val url = url(story, site)
        return "${story.title} $url"
    }

    private fun url(story: Story, site: Site): String =
        bitly.get(site).shorten("${site.websiteUrl}${story.slug}?utm_source=twitter")

    private fun supportsTelegram(site: Site): Boolean =
        site.attributes.find { SiteAttribute.TELEGRAM_ENABLED.urn == it.urn }?.value == "true"

    private fun chatId(site: Site): String? =
        site.attributes.find { SiteAttribute.TELEGRAM_CHAT_ID.urn == it.urn }?.value

    private fun token(site: Site): String? =
        site.attributes.find { SiteAttribute.TELEGRAM_TOKEN.urn == it.urn }?.value
}
