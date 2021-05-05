package com.wutsi.telegram.event

import com.wutsi.story.event.StoryEventPayload
import com.wutsi.story.event.StoryEventType
import com.wutsi.stream.Event
import com.wutsi.telegram.delegate.ShareDelegate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    @Autowired private val shareDelegate: ShareDelegate
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventHandler::class.java)
    }

    @EventListener
    fun onEvent(event: Event) {
        LOGGER.info("onEvent(${event.type}, ...)")

        if (event.type == StoryEventType.PUBLISHED.urn) {
            val payload = event.payloadAs(StoryEventPayload::class.java)
            shareDelegate.invoke(payload.storyId)
        } else {
            LOGGER.info("Event ignored")
        }
    }
}
