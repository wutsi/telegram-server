package com.wutsi.telegram.config

import com.wutsi.stream.EventStream
import com.wutsi.stream.EventSubscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MQueueConfiguration(
    @Autowired private val eventStream: EventStream
) {
    @Bean
    fun wutsiBlogServiceSubscription() = EventSubscription("wutsi-blog-service", eventStream)
}
